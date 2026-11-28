# Plan de Continuación — Nutricional Coach

> Documento operativo para retomar el trabajo en futuras sesiones sin re-analizar ni re-planificar desde cero.  
> Referencia técnica de infraestructura: [`aws Tutorial.md`](aws%20Tutorial.md) · Mapa documental ODD: [`docs/organizacion-documentacion.md`](docs/organizacion-documentacion.md).

---

## 1. Estado base al finalizar TODO 1 y Paso 0 / TODO 3 (verificado 2026-11-28)

- **Rama base:** `main` (sincronizada tras cierre del Día 28; PR #1–#6 mergeados).
- **Artefactos vigentes:**
  - Backend JAR: `target/nutricional-coach-1.0.0-SNAPSHOT.jar` (Java 21, `49/49` tests verificados en Día 23).
  - Frontend Angular 18: `frontend/dist/frontend/browser/` (`API_BASE_URL = '/api/v1'`, `15/15` tests verificados en Día 23).
- **TODO 1 — Validación E2E del Backend `prod` en Emulador Local ($0): COMPLETADO Y VERIFICADO.**
  - Stack Docker en `C:\lab-aws-nutricional\docker-compose.yml`: `postgres_rds_mock` (`postgres:16-alpine`, `:5432`), `nginx_ec2_mock` (`nginx:alpine`, `:80`) y `localstack_s3` (`localstack/localstack:4.14.0`, `14566:4566`).
  - Esquema Hibernate creado con `PROD_JPA_DDL_AUTO=update` y validado sin errores con `PROD_JPA_DDL_AUTO=validate` (4 tablas en PostgreSQL: `pacientes`, `mediciones_biometricas`, `revisiones`, `recordatorios`).
  - Flujo E2E verificado a través de Nginx (`http://localhost/`): SPA Angular `200`, bloqueo `/h2-console` (`404`), alta de pacientes (`201`), 2 mediciones (`201`), revisión mensual (`201`), descarga de PDF OpenPDF (`200 application/pdf`, `1079` bytes), `GET /recordatorios` (`200`), baja lógica `DELETE /pacientes/{id}` (`204`, fila conservada en `psql` con `activo = false` y `fecha_baja` poblada), persistencia confirmada tras reiniciar el JAR en modo `validate`, y prueba S3 en LocalStack (`s3://nutricional-coach-pdfs/revisiones/test-s3.txt`).
- **TODO 3 — Limpieza y alineación base ODD: COMPLETADO (2026-11-28):** `PROJECT_INDEX.md` y `odd/task/estado-actual-proyecto.md` actualizados al estado post-MVP, `PLAN_PENDIENTE_MVP.md` congelado como histórico MVP y `plan.md` indexado en `docs/organizacion-documentacion.md`.

> **Nota operativa al iniciar cualquier nueva sesión:** si hubo reinicio de PC o del servidor de sesión, `//./pipe/docker_engine` puede quedar detenido y LocalStack Community reinicia buckets en memoria. Comprobar primero `docker compose ps` desde `C:\lab-aws-nutricional\`, abrir Docker Desktop si hace falta, ejecutar `docker compose up -d` y verificar/recrear el bucket (`docker exec localstack_s3 awslocal s3 mb s3://nutricional-coach-pdfs --region eu-west-1`).

---

## 2. Próximos TODOs (orden recomendado)

### TODO 2 *(Opcional)* — Integrar SDK real de AWS S3 (`software.amazon.awssdk:s3`)
- **Objetivo:** Reemplazar el almacenamiento en memoria de `S3PdfStorageService.java` por subida/lectura real en S3/LocalStack manteniendo compatibilidad local por defecto (`aws.s3.enabled=false`).
- **Flujo ODD:** Crear rama **`feat/s3-sdk-integration`** desde `main` → entregar vía **PR #7**.
- **Riesgo:** `Risk: item 5` (incorpora nueva dependencia en `pom.xml`) → requiere **verificación independiente** antes de abrir PR. *(Si ocupa más de una sesión, crear `odd/tasks/s3-sdk-integration.md` antes del primer write; si entra en una sola sesión, trabajar inline).*
- **Pasos de implementación (TDD `RED → GREEN → refactor`):**
  1. **Dependencias (`pom.xml`):** añadir `software.amazon.awssdk:bom` en `<dependencyManagement>` y `software.amazon.awssdk:s3` en `<dependencies>`.
  2. **Propiedades (`src/main/resources/application.properties` y `.env.example`):** añadir:
     - `aws.s3.endpoint=${AWS_S3_ENDPOINT:}` (vacío en AWS real; `http://localhost:14566` en LocalStack Windows).
     - `aws.s3.pathStyleAccess=${AWS_S3_PATH_STYLE_ACCESS:false}` (`true` en LocalStack).
     - Mantener `aws.s3.enabled=${AWS_S3_ENABLED:false}` por defecto.
  3. **Configuración condicional (`S3ClientConfig`):** `@Configuration` con `@ConditionalOnProperty(name = "aws.s3.enabled", havingValue = "true")` que construya el bean `S3Client`:
     - Si `aws.accessKeyId` y `aws.secretAccessKey` vienen informados → `StaticCredentialsProvider` (LocalStack).
     - Si vienen vacíos → `DefaultCredentialsProvider` (compatible con **IAM Role** en EC2 sin almacenar claves).
     - Aplicar `endpointOverride(URI.create(endpoint))` solo cuando `endpoint` no esté vacío, y `forcePathStyle(pathStyleAccess)`.
  4. **Adaptador (`src/main/java/com/nutricionalcoach/service/S3PdfStorageService.java`):** inyectar `S3Client` y sustituir `ConcurrentHashMap` por `putObject` (`contentType("application/pdf")`) y `getObjectAsBytes` (capturando `NoSuchKeyException` para devolver `Optional.empty()`).
  5. **Tests y verificación en vivo:**
     - Actualizar `src/test/java/com/nutricionalcoach/service/PdfStorageServiceTest.java`: tests unitarios con Mockito sobre `S3Client` + incluir `S3ClientConfig.class` en `ApplicationContextRunner` verificando que con `aws.s3.enabled=false` no se instancia `S3Client`.
     - Compilar y empaquetar: `C:\Users\alvar\bin\mvn21.cmd clean package` (suite completa en verde).
     - Probar contra LocalStack en vivo arrancando el JAR con Java 21 explícito y `--AWS_S3_ENABLED=true --AWS_S3_ENDPOINT=http://localhost:14566 --AWS_S3_PATH_STYLE_ACCESS=true --AWS_ACCESS_KEY_ID=test --AWS_SECRET_ACCESS_KEY=test`, descargar un PDF vía `http://localhost/api/v1/revisiones/{id}/pdf` y verificar el objeto físico con `docker exec localstack_s3 awslocal s3 ls s3://nutricional-coach-pdfs/revisiones/`.
     - Ejecutar verificación independiente (`Risk: item 5`), actualizar documentación afectada y abrir/mergear **PR #7** a `main`.

---

### TODO 3 — Limpieza y alineación base ODD (documentación) `[COMPLETADO 2026-11-28]`
- **Objetivo:** Eliminar contradicciones históricas detectadas entre documentos tras completar el MVP y la preparación cloud.
- **Resultado:** `PROJECT_INDEX.md` y `odd/task/estado-actual-proyecto.md` actualizados al estado post-MVP, `PLAN_PENDIENTE_MVP.md` congelado como histórico MVP y `plan.md` añadido al mapa en `docs/organizacion-documentacion.md`.

---

### TODO 4 — Despliegue real en AWS Free Tier
- **Objetivo:** Pasar el stack validado del emulador local a una cuenta real de AWS (`eu-west-1`) siguiendo la **Parte 2** de `aws Tutorial.md`.
- **Riesgo:** `Risk: item 2 y 5` (seguridad, red, credenciales y costes). Nunca commitear archivos `.pem`, contraseñas RDS ni access keys.
- **Secuencia estricta:**
  1. **Fase 0 — Presupuesto:** crear AWS Budget (`$1.00 USD`) y fijar región `eu-west-1`.
  2. **Fase 1 — Security Groups:** `nutricional-coach-ec2-sg` (`22` solo `My IP`, `80` y `443` abiertos) y `nutricional-coach-rds-sg` (`5432` solo desde `nutricional-coach-ec2-sg`).
  3. **Fase 2 — Amazon RDS PostgreSQL:** `db.t3.micro` / `db.t4g.micro`, `20 GiB`, *storage autoscaling* desactivado, **`Public access: No`**, base `nutricionaldb`.
  4. **Fase 3 — Amazon S3 privado + IAM Role:** crear bucket privado (`Block all public access`) y asociar IAM Role acotado a la EC2 (si se completó TODO 2).
  5. **Fase 4–6 — EC2 + Nginx + `systemd`:** Ubuntu 24.04 LTS, crear `2 GB` swap, instalar `openjdk-21-jre-headless` y `nginx`, transferir `target/nutricional-coach-1.0.0-SNAPSHOT.jar` y `frontend/dist/frontend/browser/`, arrancar `systemd` con `PROD_JPA_DDL_AUTO=update` → cambiar a `validate`, y ejecutar smoke test desde `http://<EC2_PUBLIC_IP>/`.

---

## 3. Cómo reflejar cada TODO en la documentación

| Documento | Qué registrar exactamente | Cuándo editar |
|---|---|---|
| `todo` | Checklist corto de la sesión actual | En cada cierre de sesión |
| `TODO.md` | Backlog activo post-MVP | Al completar cada TODO |
| `docs/CHANGELOG.md` | Resumen por hito: *Cambios / Dificultades / Por qué / Riesgo* (sin repetir comandos largos) | Al cerrar jornada o PR |
| `aws Tutorial.md` | Solo ajustes técnicos reales (en TODO 2: quitar aviso de S3 en memoria y documentar `AWS_S3_ENDPOINT` + `AWS_S3_PATH_STYLE_ACCESS`) | Cuando cambie un paso o parámetro real |
| `DEV_RUNBOOK.md` | Comandos, nuevas variables de entorno y gotchas vigentes | En TODO 2 (vars S3) y TODO 4 (entorno prod) |
| `PROJECT_INDEX.md` y `odd/task/estado-actual-proyecto.md` | Arquitectura real y estado base ODD | En TODO 3 (limpieza) y tras TODO 2 / TODO 4 |
| `PLAN_PENDIENTE_MVP.md` | Histórico MVP | Congelar en TODO 3 |
| `docs/openapi.yaml` y `README.md` | Contratos REST y bitácora histórica | **No tocar** (no cambian endpoints) |
| **Engram** (`mem_save` + `mem_session_summary`) | Decisiones, gotchas no obvios y resumen de cierre | Tras hitos relevantes y al cerrar cada sesión |

---

## 4. Invariantes y reglas del proyecto (no romper)

- **Archivos intocables:** no modificar ni commitear `opencode.json` ni el submódulo `spring-petclinic/` (Java 17 + Gradle).
- **Java 21 explícito:** usar siempre `C:\Users\alvar\bin\mvn21.cmd` para Maven y `C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot\bin\java.exe` para ejecutar el JAR (el `java` del `PATH` apunta a JDK 17).
- **Soft-delete obligatorio:** nunca eliminar físicamente registros en `pacientes` (`activo = false` + `fechaBaja`).
- **LocalStack en Windows:** mantener `localstack/localstack:4.14.0` y mapeo `14566:4566` en `C:\lab-aws-nutricional\docker-compose.yml` para evitar `LOCALSTACK_AUTH_TOKEN` (`exit code 55`) y rangos TCP excluidos de WinNAT.
- **PowerShell 5.1:** no usar `&&` (usar `$LASTEXITCODE -eq 0`), usar el pipeline sin comillas en Git Bash para `git credential fill` sin imprimir tokens, y validar salidas vacías explícitamente en lugar de `-notmatch` directo.
