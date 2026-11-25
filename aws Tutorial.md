# Guía Paso a Paso — Emulador AWS Local (Gasto $0) y Despliegue en AWS Free Tier

Guía operativa para probar el stack completo de **Nutricional Coach** (**Spring Boot 3.4 + Java 21 + Angular 18 + PostgreSQL + S3 + Nginx**) primero en un **emulador local en su PC con Docker ($0 de coste)** y, una vez validado, pasarlo a **AWS Free Tier (EC2 + RDS + S3)** usando las configuraciones reales del proyecto (`application-prod.properties`, `.env.example` y `API_BASE_URL = '/api/v1'`).

> [!IMPORTANT]
> **Notas técnicas previas:**
> 1. **URL de API en el frontend:** `frontend/src/app/core/api.config.ts` ya está configurado con `API_BASE_URL = '/api/v1'` (compatible con `frontend/proxy.conf.json` en local y con proxy inverso Nginx sin errores CORS).
> 2. **Estado de S3 en el código:** `S3PdfStorageService` gestiona el conmutador `AWS_S3_ENABLED` y el formato de URI `s3://<bucket>/revisiones/revision-<id>.pdf`, pero almacena en memoria sin dependencia del SDK oficial de AWS (`software.amazon.awssdk:s3`). Tanto en el emulador local como en AWS real, mantenga `AWS_S3_ENABLED=false` hasta incorporar el cliente S3 real si requiere persistencia en bucket.
> 3. **Alcance de LocalStack Community Edition:** La edición gratuita de LocalStack emula **S3** en el puerto `4566`, pero **no** emula Amazon RDS PostgreSQL (exclusivo de LocalStack Pro). Por ello, el entorno local combina **LocalStack (S3)** + **PostgreSQL 16 (emulando RDS)** + **Nginx (emulando EC2 Reverse Proxy)** en Docker Compose.
> 4. **Compilación fuera de EC2:** En instancias Free Tier de 1 GB de RAM (`t2.micro`/`t3.micro`), compile el JAR y el bundle de Angular en su equipo local y transfiera únicamente los artefactos compilados.

---

## Arquitectura Equivalente: Emulador Local vs. AWS Real

| Componente | En Emulador Local (Docker, $0) | En AWS Free Tier |
|---|---|---|
| **Proxy inverso + SPA** | Contenedor `nginx:alpine` (`http://localhost:80`) | Nginx en EC2 (`http://<EC2_PUBLIC_IP>:80`) |
| **Backend API (`prod`)** | Fat JAR en local (`127.0.0.1:8080`, perfil `prod`) | Fat JAR en EC2 vía `systemd` (`127.0.0.1:8080`, perfil `prod`) |
| **Base de datos** | Contenedor `postgres:16-alpine` (`localhost:5432`) | Amazon RDS PostgreSQL (`Public access: No`, `:5432`) |
| **Almacenamiento S3** | Contenedor `localstack/localstack:4.14.0` (`http://localhost:14566`) | Amazon S3 privado (`eu-west-1`) |

---

# Parte 1 — Emulador AWS Local en su PC (Gasto $0 con Docker)

Requisitos ya disponibles en su equipo: **Docker Desktop** (`docker --version` / `docker compose version`), **Java 21** (`C:\Users\alvar\bin\mvn21.cmd`) y **Node.js / npm**.

## Paso 1 — Empaquetar primero el Backend y el Frontend

Antes de montar los contenedores, genere los artefactos reales de producción desde la raíz del proyecto:

```powershell
# 1. Compilar y empaquetar Backend (genera target/nutricional-coach-1.0.0-SNAPSHOT.jar)
C:\Users\alvar\bin\mvn21.cmd clean package -DskipTests

# 2. Compilar Frontend Angular 18 (genera frontend/dist/frontend/browser/)
Set-Location frontend
npm run build
Set-Location ..
```

## Paso 2 — Crear la carpeta de laboratorio Docker (fuera de Git)

Para no mezclar archivos temporales de laboratorio con el repositorio, cree una carpeta separada (por ejemplo `C:\lab-aws-nutricional\`) con estos dos archivos:

### 2.1 Archivo `C:\lab-aws-nutricional\nginx.conf`

```nginx
server {
    listen 80;
    server_name localhost;

    root /usr/share/nginx/html;
    index index.html;

    # Routing SPA de Angular 18
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Proxy inverso hacia Spring Boot corriendo en el host Windows (:8080)
    location /api/v1 {
        proxy_pass http://host.docker.internal:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 2.2 Archivo `C:\lab-aws-nutricional\docker-compose.yml`

Sustituya la ruta de `frontend/dist/frontend/browser` si su repositorio está en otra ubicación:

```yaml
services:
  # 1. Emulador de Amazon RDS PostgreSQL
  postgres-rds:
    image: postgres:16-alpine
    container_name: postgres_rds_mock
    environment:
      POSTGRES_DB: nutricionaldb
      POSTGRES_USER: nutricional_admin
      POSTGRES_PASSWORD: local_rds_password
    ports:
      - "5432:5432"
    volumes:
      - pg_rds_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U nutricional_admin -d nutricionaldb"]
      interval: 5s
      timeout: 5s
      retries: 10

  # 2. Emulador de Amazon S3 (LocalStack Community Edition)
  # Nota: se fija :4.14.0 (última release Community sin LOCALSTACK_AUTH_TOKEN obligatorio)
  # y se expone 14566:4566 en host para evitar rangos de puertos excluidos de Windows WinNAT (4484-4583).
  localstack:
    image: localstack/localstack:4.14.0
    container_name: localstack_s3
    ports:
      - "14566:4566"
    environment:
      - SERVICES=s3
      - AWS_DEFAULT_REGION=eu-west-1
    volumes:
      - localstack_data:/var/lib/localstack

  # 3. Emulador de Nginx Reverse Proxy en EC2 (puerto 80)
  nginx-ec2:
    image: nginx:alpine
    container_name: nginx_ec2_mock
    ports:
      - "80:80"
    volumes:
      - "G:/Proyectos OpenCode/BBVA/Nutritional Coach/frontend/dist/frontend/browser:/usr/share/nginx/html:ro"
      - "./nginx.conf:/etc/nginx/conf.d/default.conf:ro"
    extra_hosts:
      - "host.docker.internal:host-gateway"

volumes:
  pg_rds_data:
  localstack_data:
```

## Paso 3 — Levantar el emulador e inicializar el bucket S3

Ejecute desde `C:\lab-aws-nutricional\`:

```powershell
# Levantar PostgreSQL, LocalStack (S3) y Nginx
docker compose up -d

# Verificar que los 3 contenedores están activos y saludables
docker compose ps
```

Cree el bucket `nutricional-coach-pdfs` dentro de LocalStack usando `awslocal` (incluido en el propio contenedor, sin necesidad de instalar AWS CLI en Windows):

```powershell
# Crear bucket y comprobar listado en LocalStack S3
docker exec localstack_s3 awslocal s3 mb s3://nutricional-coach-pdfs --region eu-west-1
docker exec localstack_s3 awslocal s3 ls
```

> [!NOTE]
> - En LocalStack Community Edition, los buckets y objetos de S3 se reinician al recrear el contenedor. Si reinicia desde cero (`docker compose down` + `up`), vuelva a ejecutar `awslocal s3 mb s3://nutricional-coach-pdfs --region eu-west-1`. Los datos de PostgreSQL sí persisten en el volumen `pg_rds_data`.
> - Si prefiere usar `localstack/localstack:latest` (versiones `2026.03+`), genere un token gratuito en `https://app.localstack.cloud/` y páselo como `LOCALSTACK_AUTH_TOKEN`; con `4.14.0` no se requiere token.
> - Si en Windows algún puerto sigue dando `bind: An attempt was made to access a socket in a way forbidden by its access permissions`, revise `netsh interface ipv4 show excludedportrange protocol=tcp` y use otro puerto de host libre (en este equipo `4566` caía dentro de `4484–4583`, por eso se mapea `14566:4566`).

## Paso 4 — Arrancar el Backend en perfil `prod` contra el RDS emulado

> [!WARNING]
> **Precedencia de variables frente al `.env` local:** El `.env` de la raíz del proyecto define `DB_URL=jdbc:h2:mem:...`. En Spring Boot, los **argumentos de línea de comandos (`--VAR=...`)** o las **variables de entorno del sistema** tienen mayor prioridad que `.env`, por lo que NO debe editar ni borrar su `.env` local de H2.

### 4.1 Primer arranque (crear esquema de tablas con `PROD_JPA_DDL_AUTO=update`)

> [!NOTE]
> Use explícitamente el ejecutable de **Java 21** (`C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot\bin\java.exe`), ya que el `java` por defecto del `PATH` en este equipo apunta a JDK 17 (requerido por `spring-petclinic/`).

Desde la raíz del repositorio en PowerShell:

```powershell
& "C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot\bin\java.exe" -Xms256m -Xmx512m -jar target/nutricional-coach-1.0.0-SNAPSHOT.jar `
  --spring.profiles.active=prod `
  --DB_URL=jdbc:postgresql://localhost:5432/nutricionaldb `
  --DB_USER=nutricional_admin `
  --DB_PASSWORD=local_rds_password `
  --PROD_JPA_DDL_AUTO=update `
  --AWS_S3_ENABLED=false
```

Compruebe en una segunda terminal que Hibernate creó las 4 tablas reales en PostgreSQL:

```powershell
docker exec -it postgres_rds_mock psql -U nutricional_admin -d nutricionaldb -c "\dt"
```

Debe ver las tablas: `mediciones_biometricas`, `pacientes`, `recordatorios` y `revisiones`.

### 4.2 Segundo arranque (modo estricto `PROD_JPA_DDL_AUTO=validate`, igual que en RDS real)

Detenga el JAR con `Ctrl+C` y vuelva a arrancarlo simulando el modo endurecido de producción (`validate`):

```powershell
& "C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot\bin\java.exe" -Xms256m -Xmx512m -jar target/nutricional-coach-1.0.0-SNAPSHOT.jar `
  --spring.profiles.active=prod `
  --DB_URL=jdbc:postgresql://localhost:5432/nutricionaldb `
  --DB_USER=nutricional_admin `
  --DB_PASSWORD=local_rds_password `
  --PROD_JPA_DDL_AUTO=validate `
  --AWS_S3_ENABLED=false
```

## Paso 5 — Ejecutar todas las pruebas E2E en el emulador (sin tocar AWS real)

Con los 3 contenedores arriba y el JAR corriendo en perfil `prod`:

1. **Verificar bloqueo de consola H2:**
   - Abra `http://localhost:8080/h2-console` o `http://localhost/h2-console`. En perfil `prod`, la consola H2 **no** debe estar disponible (`404` o redirigido a la SPA).
2. **Verificar flujo completo desde el navegador (Nginx `:80` → JAR `:8080` → PostgreSQL `:5432`):**
   - Abra `http://localhost/` (sin puerto `:4200` ni `:8080`: está probando el bundle compilado servido por Nginx).
   - Cree un paciente, registre dos mediciones biométricas, genere una revisión mensual y descargue el PDF.
3. **Verificar persistencia real en PostgreSQL:**
   - Ejecute:
     ```powershell
     docker exec -it postgres_rds_mock psql -U nutricional_admin -d nutricionaldb -c "SELECT id, nombre, apellidos, activo FROM pacientes;"
     ```
   - Reinicie el JAR (`Ctrl+C` y vuelva a lanzar con `PROD_JPA_DDL_AUTO=validate`) y recargue `http://localhost/`. A diferencia de H2 en memoria, sus pacientes, mediciones y revisiones deben seguir ahí.
4. **Probar operaciones S3 simuladas en LocalStack (ejecutar desde `C:\lab-aws-nutricional\`):**
   ```powershell
   "PDF test" | Out-File -Encoding utf8 test-s3.txt
   docker cp test-s3.txt localstack_s3:/tmp/test-s3.txt
   docker exec localstack_s3 awslocal s3 cp /tmp/test-s3.txt s3://nutricional-coach-pdfs/revisiones/test-s3.txt
   docker exec localstack_s3 awslocal s3 ls s3://nutricional-coach-pdfs/revisiones/
   ```
   *(Si más adelante integra `software.amazon.awssdk:s3` en `S3PdfStorageService.java`, bastará añadir una propiedad opcional `AWS_S3_ENDPOINT=http://localhost:14566` para que el propio backend Java suba los PDFs generados directamente a LocalStack).*

## Paso 6 — Apagar y limpiar el laboratorio local

```powershell
# Detener el JAR con Ctrl+C en su terminal, y luego desde C:\lab-aws-nutricional\:
docker compose down          # Detiene contenedores conservando datos de PostgreSQL
# docker compose down -v     # Borra también los volúmenes (reset limpio de BD)
```

---

# Parte 2 — Despliegue Real en AWS Free Tier

Una vez validado todo en el emulador local, utilice estos pasos para desplegar en su cuenta real de AWS (`eu-west-1`).

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
