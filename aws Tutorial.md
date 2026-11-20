# Guía Paso a Paso — Configuración y Despliegue en AWS Free Tier (Nutricional Coach)

Guía operativa para aprovisionar la infraestructura en **AWS (EC2 + RDS PostgreSQL + S3)** y desplegar el backend **Spring Boot 3.4 (Java 21)** junto al frontend **Angular 18** usando las configuraciones reales del proyecto (`application-prod.properties` y `.env.example`).

> [!IMPORTANT]
> **Notas técnicas previas al despliegue:**
> 1. **URL de API en el frontend:** `frontend/src/app/core/api.config.ts` ya está configurado con `API_BASE_URL = '/api/v1'` (compatible con `frontend/proxy.conf.json` en local y con proxy inverso Nginx en EC2 sin errores CORS).
> 2. **Estado de S3 en el código:** `S3PdfStorageService` gestiona el conmutador `AWS_S3_ENABLED` y el formato de URI `s3://<bucket>/revisiones/revision-<id>.pdf`, pero almacena en memoria sin dependencia del SDK oficial de AWS (`software.amazon.awssdk:s3`). En producción actual, mantenga `AWS_S3_ENABLED=false` hasta incorporar el cliente S3 real si requiere persistencia en bucket.
> 3. **Compilación fuera de EC2:** En instancias Free Tier de 1 GB de RAM (`t2.micro`/`t3.micro`), compile el JAR y el bundle de Angular en su equipo local y transfiera únicamente los artefactos compilados.

---

## Arquitectura Objetivo en AWS

```text
Internet (HTTP/HTTPS :80/:443)
        │
        ▼
┌─────────────────────────────────────────────┐
│ EC2 (Ubuntu 24.04 / Amazon Linux 2023)      │
│  ├─ Nginx (:80)                             │
│  │   ├─ /          ──► /var/www/... (SPA)   │
│  │   └─ /api/v1    ──► proxy_pass :8080     │
│  └─ Spring Boot JAR (:8080, profile=prod)   │
└──────────────────┬──────────────────────────┘
                   │ TCP :5432 (solo Security Group EC2)
                   ▼
┌─────────────────────────────────────────────┐
│ Amazon RDS PostgreSQL (Public access: No)   │
│  └─ Base de datos: nutricionaldb            │
└─────────────────────────────────────────────┘

Amazon S3 (privado): nutricional-coach-pdfs (región eu-west-1)
```

---

## Fase 0 — Presupuesto y Control de Costos (Obligatorio)

1. Ingrese en **AWS Console → Billing and Cost Management → Budgets → Create budget**.
2. Seleccione **Zero spend budget** o **Monthly cost budget** con umbral de **$1.00 USD** y su correo de notificación.
3. Verifique bajo qué modalidad opera su cuenta:
   - **Cuenta creada desde el 15 de julio de 2025 (*Free Plan*):** dispone de hasta **$200 USD en créditos** durante 6 meses para EC2, RDS y S3 sin cargos automáticos fuera del crédito mientras permanezca en *Free Plan*.
   - **Cuenta anterior (*Free Tier clásico 12 meses*):** incluye **750 h/mes** de EC2 (`t2.micro`/`t3.micro`), **750 h/mes** de RDS (`db.t2.micro`/`db.t3.micro`, 20 GB) y **5 GB** en S3.
4. Seleccione la región **`eu-west-1` (Irlanda)** en la esquina superior derecha de la consola (región por defecto configurada en `.env.example`), o ajuste `AWS_REGION` de forma coherente en todos los recursos.

---

## Fase 1 — Security Groups (Firewall de VPC)

Cree los dos Security Groups en **EC2 → Network & Security → Security Groups** antes de levantar instancias.

| Security Group | Regla | Tipo / Puerto | Origen (Source) | Propósito |
|---|---|---|---|---|
| `nutricional-coach-ec2-sg` | Inbound 1 | SSH (`22`) | **My IP** (`/32`) | Administración segura por SSH |
| `nutricional-coach-ec2-sg` | Inbound 2 | HTTP (`80`) | `0.0.0.0/0` | Tráfico web hacia Nginx |
| `nutricional-coach-ec2-sg` | Inbound 3 | HTTPS (`443`) | `0.0.0.0/0` | Tráfico TLS (si añade Certbot/SSL) |
| `nutricional-coach-rds-sg` | Inbound 1 | PostgreSQL (`5432`) | **`nutricional-coach-ec2-sg`** | Acceso exclusivo desde la EC2 |

> [!CAUTION]
> Nunca abra el puerto `5432` de RDS hacia `0.0.0.0/0` ni el puerto `8080` hacia Internet; Tomcat debe responder únicamente a Nginx en `127.0.0.1:8080`.

---

## Fase 2 — Amazon RDS PostgreSQL (Base de Datos)

1. Abra **RDS → Databases → Create database**.
2. Configure los siguientes parámetros:
   - **Creation method:** `Standard create`
   - **Engine type:** `PostgreSQL` (compatible con el driver `42.7.4` incluido en `pom.xml`)
   - **Templates:** `Free tier`
   - **DB instance identifier:** `nutricional-coach-db`
   - **Master username:** `nutricional_admin` (anote usuario y contraseña)
   - **Instance configuration:** `db.t3.micro` o `db.t4g.micro` (clase elegible en Free Tier)
   - **Storage:** `20 GiB` General Purpose SSD (`gp2` o `gp3`). **Desmarque** *Enable storage autoscaling* para evitar ampliaciones automáticas fuera de cuota.
   - **Connectivity → Public access:** **`No`** (evita cargos de IPv4 pública de ~$0.005/h y protege la base de datos en red privada).
   - **VPC security group:** seleccione `nutricional-coach-rds-sg`.
   - **Additional configuration → Initial database name:** **`nutricionaldb`**
3. Pulse **Create database** y espere a que el estado sea *Available*.
4. Copie el **Endpoint** de la pestaña *Connectivity & security* (ejemplo: `nutricional-coach-db.xxxxxxxxxxxx.eu-west-1.rds.amazonaws.com`).

---

## Fase 3 — Amazon S3 (Bucket Privado para PDFs)

1. Abra **S3 → Create bucket** en la región **`eu-west-1`**.
2. **Bucket name:** `nutricional-coach-pdfs` (los nombres de S3 son globales; si ya está ocupado, use por ejemplo `nutricional-coach-pdfs-<iniciales-año>` y actualice `AWS_S3_BUCKET`).
3. **Block Public Access settings:** mantenga marcado **Block all public access**.
4. **Bucket Versioning / Encryption:** mantenga el cifrado SSE-S3 por defecto. Pulse **Create bucket**.
5. *(Para cuando se conecte el SDK de AWS S3)*: asigne un **IAM Role** a la EC2 con permisos acotados al bucket (`s3:PutObject`, `s3:GetObject`, `s3:DeleteObject` sobre `arn:aws:s3:::<bucket>/revisiones/*` y `s3:ListBucket` sobre `arn:aws:s3:::<bucket>`), evitando almacenar claves de usuario raíz en el servidor.

---

## Fase 4 — Amazon EC2 (Servidor de Aplicación + Nginx)

### 4.1 Lanzar la instancia
1. Abra **EC2 → Instances → Launch instances**.
2. **Name:** `nutricional-coach-app`.
3. **AMI:** `Ubuntu Server 24.04 LTS` (64-bit x86).
4. **Instance type:** `t3.micro` o `t2.micro` (1 vCPU, 1 GB RAM; o `t3.small` si utiliza créditos del *Free Plan*).
5. **Key pair:** cree un par de claves (`nutricional-coach-key.pem`) y guárdelo en un lugar seguro.
6. **Network settings:** seleccione *Auto-assign public IP: Enable* y el Security Group `nutricional-coach-ec2-sg`.
7. **Configure storage:** `20 GiB gp3` (dentro de los 30 GB de EBS gratuitos).
8. Pulse **Launch instance** y anote la **Public IPv4 address**.

### 4.2 Preparar el sistema operativo (SSH)
Conéctese por SSH desde su equipo:

```bash
ssh -i "nutricional-coach-key.pem" ubuntu@<EC2_PUBLIC_IP>
```

Ejecute dentro de la EC2 la creación de **2 GB de swap** (fundamental en instancias de 1 GB de RAM) e instale **Java 21** y **Nginx**:

```bash
# 1. Crear archivo swap de 2 GB para evitar OutOfMemory en 1 GB RAM
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

# 2. Actualizar paquetes e instalar Java 21 (headless) y Nginx
sudo apt update && sudo apt upgrade -y
sudo apt install -y openjdk-21-jre-headless nginx

# 3. Verificar versiones y crear directorios de la aplicación
java -version
nginx -v
sudo mkdir -p /opt/nutricional-coach /var/www/nutricional-coach
sudo chown -R ubuntu:ubuntu /opt/nutricional-coach /var/www/nutricional-coach
```

---

## Fase 5 — Variables de Entorno y Servicio `systemd`

### 5.1 Crear `/opt/nutricional-coach/.env`
`application.properties` importa automáticamente `.env` desde el directorio de trabajo (`spring.config.import=optional:file:.env[.properties]`), mientras que `application-prod.properties` exige `DB_URL`, `DB_USER`, `DB_PASSWORD` y desactiva `/h2-console` (`spring.h2.console.enabled=false`).

Cree `/opt/nutricional-coach/.env`:

```bash
cat << 'EOF' > /opt/nutricional-coach/.env
SERVER_PORT=8080

# PostgreSQL en Amazon RDS
DB_URL=jdbc:postgresql://<RDS_ENDPOINT>:5432/nutricionaldb
DB_USER=nutricional_admin
DB_PASSWORD=<SU_PASSWORD_RDS>

# Solo en el PRIMER arranque para crear las tablas en RDS; luego cambiar a validate
PROD_JPA_DDL_AUTO=update

# AWS Config
AWS_REGION=eu-west-1
AWS_S3_ENABLED=false
AWS_S3_BUCKET=nutricional-coach-pdfs
EOF

chmod 600 /opt/nutricional-coach/.env
```

### 5.2 Configurar servicio `systemd`
Cree `/etc/systemd/system/nutricional-coach.service` acotando el heap de la JVM para convivir con Nginx en 1 GB de RAM:

```ini
[Unit]
Description=Nutricional Coach Spring Boot API
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/opt/nutricional-coach
ExecStart=/usr/bin/java -Xms256m -Xmx512m -jar /opt/nutricional-coach/nutricional-coach-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

---

## Fase 6 — Empaquetado Local, Despliegue y Nginx

### 6.1 Compilar artefactos en su equipo local
1. Verifique que `frontend/src/app/core/api.config.ts` exporte `export const API_BASE_URL = '/api/v1';` (configurado en la rama actual para resolver peticiones al mismo origen detrás de Nginx).
2. Ejecute las compilaciones en su equipo local:

```powershell
# Desde la raíz del repositorio (Backend fat JAR)
C:\Users\alvar\bin\mvn21.cmd clean package -DskipTests

# Desde frontend/ (Frontend Angular 18 optimizado para producción)
cd frontend
npm run build
```

### 6.2 Subir artefactos a EC2

```bash
# Subir JAR del backend
scp -i "nutricional-coach-key.pem" target/nutricional-coach-1.0.0-SNAPSHOT.jar ubuntu@<EC2_PUBLIC_IP>:/opt/nutricional-coach/

# Subir estáticos de Angular 18
scp -i "nutricional-coach-key.pem" -r frontend/dist/frontend/browser/* ubuntu@<EC2_PUBLIC_IP>:/var/www/nutricional-coach/
```

### 6.3 Configurar Nginx como Reverse Proxy
En la sesión SSH de la EC2, configure `/etc/nginx/sites-available/nutricional-coach`:

```nginx
server {
    listen 80 default_server;
    server_name _;

    root /var/www/nutricional-coach;
    index index.html;

    # Routing SPA de Angular 18
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Proxy inverso hacia Spring Boot
    location /api/v1 {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Habilite el sitio, valide la sintaxis y arranque ambos servicios:

```bash
sudo rm -f /etc/nginx/sites-enabled/default
sudo ln -sf /etc/nginx/sites-available/nutricional-coach /etc/nginx/sites-enabled/nutricional-coach
sudo nginx -t
sudo systemctl restart nginx

sudo systemctl daemon-reload
sudo systemctl enable --now nutricional-coach
sudo journalctl -u nutricional-coach -f
```

### 6.4 Endurecimiento tras el primer arranque (Hibernate `validate`)
Una vez confirmado en los logs que Hibernate creó las tablas (`pacientes`, `mediciones_biometricas`, `revisiones`, `recordatorios`) y el backend respondió `Started NutricionalCoachApplication`:

1. Edite `/opt/nutricional-coach/.env` y cambie `PROD_JPA_DDL_AUTO=update` por `PROD_JPA_DDL_AUTO=validate`.
2. Reinicie el servicio: `sudo systemctl restart nutricional-coach`.

---

## Checklist de Verificación Post-Despliegue

- [ ] `curl -i http://<EC2_PUBLIC_IP>/api/v1/pacientes` devuelve `200 OK` con `[]` (sin datos semilla del perfil `local`).
- [ ] `curl -i http://<EC2_PUBLIC_IP>/h2-console` **no** expone consola H2 (`404` / redirigido a SPA).
- [ ] Navegador en `http://<EC2_PUBLIC_IP>/` carga Angular 18 y permite alta de paciente, registro de mediciones y descarga de PDF.
- [ ] `PROD_JPA_DDL_AUTO` fijado en `validate` tras la creación inicial de tablas en RDS.
- [ ] Alarma de **AWS Budgets** configurada en `$1.00 USD` y RDS marcado con **Public access = No**.
