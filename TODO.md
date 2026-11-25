# TODO — Nutricional Coach (MVP)

## Hasta el MVP

### Backend (HECHO, verificado — 40/40 tests verdes)
- [x] `GET /api/v1/pacientes` — listar solo activos, paginación page/size (suite 40/40 verdes)
- [x] `GET /api/v1/pacientes/{id}` — obtener paciente por ID (200/404)
- [x] `POST /api/v1/pacientes` — alta de paciente (201 Created + Location, 400 sin nombre/email)
- [x] `DELETE /api/v1/pacientes/{id}` — soft-delete (activo=false + fechaBaja, 204/404)
- [x] `POST /api/v1/pacientes/{id}/mediciones` — registrar medición (201 Created + Location)
- [x] `GET /api/v1/pacientes/{id}/mediciones` — listar mediciones de un paciente (200)
- [x] `GET /api/v1/revisiones` y `GET /api/v1/revisiones/{id}` — revisiones mensuales (200/404)
- [x] `POST /api/v1/pacientes/{id}/revisiones` — crear revisión con deltas y ruta alineada con OpenAPI + CORS `:4200` (201 Created + Location)
- [x] `GET /api/v1/revisiones/{id}/pdf` — descargar PDF de plan de dieta/entreno (OpenPDF, 200/404)
- [x] `GET /api/v1/recordatorios` — listar recordatorios pendientes (200 con lista RecordatorioDTO)
- [x] `PATCH /api/v1/recordatorios/{id}` — actualizar feedback del paciente (200/404)

### Frontend Angular 18 (`npm run dev`) — HECHO, verificado (14/14 tests verdes)
- [x] T1: Generar proyecto Angular 18 (carpeta `frontend/`, routing, CSS, npm)
- [x] T2: Servicio HTTP base `http://localhost:8080/api/v1` (`core/api.config.ts`, `core/api.service.ts`, `provideHttpClient()` en `app.config.ts`)
- [x] T3: Páginas de pacientes (alta S1, listar S2, detalle S3)
- [x] T4: Soft-delete de paciente (S4)
- [x] T5: Mediciones biométricas por paciente (S5, S6)
- [x] T6: Revisiones mensuales (listar S7, crear S8, descargar PDF S9)
- [x] T7: Recordatorios pendientes + actualizar feedback (S10, S11)
- [x] T8: Tests que pinchen cada endpoint (200/201/404) (S1-S11)
- [x] T9: Documentar dificultades y comandos en `docs/CHANGELOG.md` (S1-S11)

### Hecho (Día 17 — 2026-11-12)
- [x] **Alinear ruta `POST /api/v1/pacientes/{id}/revisiones` en `RevisionController.java` y habilitar CORS (`http://localhost:4200`):** Ajustado `@RequestMapping("/api/v1")` en `RevisionController`, añadido `@CrossOrigin(origins = "http://localhost:4200")` en los 4 controladores REST y verificado con `MockMvc` (`40/40` tests verdes).

### Hecho (Día 18 — 2026-11-13)
- [x] **Release / PR #1 MVP mergeado en `main`:**
  - Verificadas suites completas: Backend 40/40 tests verdes (`mvn21.cmd test`) y Frontend 14/14 tests Jasmine/Karma verdes en `ChromeHeadless` con `tsc --noEmit` limpio.
  - Creado Pull Request #1 en GitHub (`https://github.com/Alvar0dev/Nutricional-Coach/pull/1`) mediante `gh pr create` con token de Git Credential Manager.
  - Pull Request #1 mergeado en `main` en GitHub (commit `462393a`).
  - Sincronizado `main` local (`git pull origin main`, fast-forward de 16 commits).
  - Creada rama aislada de trabajo `feat/springboot-entrypoint-h2` para el arranque local.
  - Diagnosticada necesidad de `com.h2database:h2` en `pom.xml`, `@SpringBootApplication` y `application.properties`.

### Hecho (Día 19 — 2026-11-14)
- [x] **1. Dependencia H2, punto de entrada `@SpringBootApplication` y `application.properties`:** Agregado `com.h2database:h2` a `pom.xml`, creada `com.nutricionalcoach.NutricionalCoachApplication`, configurado `src/main/resources/application.properties` (`jdbc:h2:mem:nutricionaldb`, `/h2-console`, `ddl-auto=update`), anotadas las 4 entidades JPA (`@Entity`, `@Table`, `@Id`), ajustado `RevisionController.crearRevision` para propagar `@PathVariable id` al DTO cuando no viene en el body, y creado `NutricionalCoachApplicationTest` (suite **41/41 tests verdes**).
- [x] **2. Prueba E2E en vivo (`:4200` ↔ `:8080`):** Levantados backend (`mvn21.cmd spring-boot:run` en `:8080`) y frontend (`npm start` en `:4200`), validando en vivo H2 Console (`200`), alta y listado de pacientes (`201`/`200` + CORS `:4200`), registro y consulta de mediciones (`201`/`200`), creación de revisión mensual y descarga de PDF OpenPDF (`201`/`200 application/pdf`, `1115 bytes`), recordatorios (`200`) y baja lógica soft-delete (`204`).

### Hecho (Día 20 — 2026-11-16)
- [x] **1. Infra / Entorno Cloud (`.env` y `.env.example`):** Creadas la plantilla `.env.example` (versionada) y `.env` local (ignorada por git) con `DB_URL`, `DB_DRIVER`, `DB_USER`, `DB_PASSWORD`, `AWS_REGION`, `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` y `AWS_S3_BUCKET`, parametrizando `src/main/resources/application.properties` (`spring.config.import=optional:file:.env[.properties]`) con fallback por defecto a H2 en memoria y verificado en `NutricionalCoachApplicationTest.resolvesCloudEnvironmentPlaceholdersWithH2Fallback` (suite **42/42 tests verdes**).
- [x] **3. Datos semilla para Demo (`DataInitializer` en perfil `local`):** Creado `com.nutricionalcoach.config.DataInitializer` (`@Profile("local")`) idempotente que precarga 1 paciente (`pac-demo-1`), 2 mediciones (`med-demo-1`, `med-demo-2`), 1 revisión (`rev-demo-1` con descarga PDF) y 1 recordatorio `PENDIENTE` (`rec-demo-1`), verificado en `DataInitializerTest` con BD H2 aislada (suite **43/43 tests verdes**).
- [x] **2. Pull Request y Merge a `main` (`feat/springboot-entrypoint-h2` → `main`):** PR #2 con punto de entrada Spring Boot, configuración H2, test E2E, entorno Cloud/`.env` y `DataInitializer` demo creado, mergeado en `main` (`033bb28`) y sincronizado localmente.

### Hecho (Día 21 — 2026-11-17)
- [x] **1. Driver PostgreSQL y perfil `prod` para AWS RDS:** Añadida dependencia `org.postgresql:postgresql` (`scope=runtime`) en `pom.xml`, creado `src/main/resources/application-prod.properties` (H2 console deshabilitada, `org.postgresql.Driver`, `DB_URL`/`DB_USER`/`DB_PASSWORD` y `ddl-auto=${PROD_JPA_DDL_AUTO:validate}`) y verificado en `ProdProfileConfigTest` (suite **45/45 tests verdes**).

### Hecho (Día 22 — 2026-11-18)
- [x] **1. Pull Request y Merge a `main` (`feat/aws-rds-prod-profile` → `main`):** Creado y mergeado PR #3 (`https://github.com/Alvar0dev/Nutricional-Coach/pull/3`, commit `897831f`), `main` sincronizada localmente y abierta rama `feat/pdf-storage-service`.
- [x] **2. Almacenamiento de PDFs en AWS S3 / local (`PdfStorageService`):** Extraída interfaz `PdfStorageService` con `LocalPdfStorageService` (por defecto, `aws.s3.enabled=false`) y `S3PdfStorageService` (`aws.s3.enabled=true`, URI `s3://<bucket>/revisiones/revision-<id>.pdf`), conectada en `RevisionServiceImpl` y verificada en `PdfStorageServiceTest` (suite **49/49 tests verdes**).

### Hecho (Día 23 — 2026-11-19)
- [x] **1. Pull Request #4 y Merge a `main` (`feat/pdf-storage-service` → `main`):** Creado y mergeado PR #4 (`https://github.com/Alvar0dev/Nutricional-Coach/pull/4`, commit `197983d`) y sincronizada `main` localmente.
- [x] **2. Empaquetado de producción y guía AWS Free Tier (`aws Tutorial.md`):** Cambiado `API_BASE_URL` a `'/api/v1'` en `frontend/src/app/core/api.config.ts` (compatible con `proxy.conf.json` y Nginx) con test TDD en `api.config.spec.ts`; validados `mvn21.cmd clean package` (**49/49 verdes**, `target/nutricional-coach-1.0.0-SNAPSHOT.jar`), `tsc --noEmit`, `ng test` (**15/15 verdes**) y `npm run build` (`frontend/dist/frontend/`); creada guía `aws Tutorial.md` y actualizado `DEV_RUNBOOK.md`.

### Hecho (Día 24 — 2026-11-20)
- [x] **1. Pull Request #5 y Merge a `main` (`feat/prod-packaging-aws-guide` → `main`):** Creado y mergeado PR #5 (`https://github.com/Alvar0dev/Nutricional-Coach/pull/5`, commit `e982e88`), sincronizada `main` localmente y abierta rama `docs/aws-local-emulator-tutorial`.
- [x] **2. Guía del Emulador AWS Local ($0 con Docker) en `aws Tutorial.md`:** Documentada la Parte 1 completa (`postgres:16-alpine` + `localstack/localstack` S3 + `nginx:alpine`), tabla comparativa local vs AWS real, ruta explícita de Java 21, flujo `PROD_JPA_DDL_AUTO=update` → `validate` y pruebas E2E/S3 locales.

### Hecho (Día 25 — 2026-11-21)
- [x] **1. Pull Request #6 y Merge a `main` (`docs/aws-local-emulator-tutorial` → `main`):** Creado y mergeado PR #6 (`https://github.com/Alvar0dev/Nutricional-Coach/pull/6`, commit `9e4b203`) y sincronizada `main` localmente (`e982e88..9e4b203`).
- [x] **2 (Preparación). Laboratorio Docker en `C:\lab-aws-nutricional\`:** Verificados artefactos compilados (`target/nutricional-coach-1.0.0-SNAPSHOT.jar` y `frontend/dist/frontend/browser/index.html`), binario Java 21 y puertos libres (`80, 4566, 5432, 8080`); creados `C:\lab-aws-nutricional\nginx.conf` y `C:\lab-aws-nutricional\docker-compose.yml` validados offline con `docker compose config --quiet`.

### Hecho hoy (Día 26 — 2026-11-24 / 2026-11-25)
- [x] **1. Arrancar Docker Desktop, levantar emulador AWS local ($0) y crear bucket S3:** Ajustado `C:\lab-aws-nutricional\docker-compose.yml` a `localstack/localstack:4.14.0` (sin token obligatorio) y puerto `14566:4566` (evitando exclusión WinNAT `4484–4583`), levantados y verificados los 3 contenedores (`postgres_rds_mock` healthy `:5432`, `nginx_ec2_mock` HTTP `200` `:80`, `localstack_s3` healthy `14566:4566` con S3 disponible) y creado bucket `s3://nutricional-coach-pdfs` (`eu-west-1`). Actualizada la guía `aws Tutorial.md` con estos hallazgos. *(Nota: tras el reinicio de sesión de hoy, Docker Desktop queda detenido hasta reabrirlo).*

### Pendientes para mañana (Emulador Local y Despliegue Cloud)
- [ ] **1. Reabrir Docker Desktop y levantar contenedores (`docker compose up -d`):** Iniciar Docker Desktop, ejecutar `docker compose up -d` en `C:\lab-aws-nutricional\` y confirmar/recrear el bucket `nutricional-coach-pdfs` (`docker exec localstack_s3 awslocal s3 mb s3://nutricional-coach-pdfs --region eu-west-1`).
- [ ] **2. Arrancar el Backend `prod` y validar flujo E2E en `http://localhost/`:** Ejecutar el JAR con Java 21 explícito en perfil `prod` (`PROD_JPA_DDL_AUTO=update` → `validate`), comprobar bloqueo de `/h2-console`, probar alta/mediciones/revisión/PDF desde Nginx (`:80`) y validar persistencia en PostgreSQL + pruebas S3 en LocalStack.
- [ ] **3. *(Opcional)* Integrar SDK real de AWS S3 (`software.amazon.awssdk:s3`):** Añadir cliente S3 en `S3PdfStorageService.java` (en nueva rama `feat/s3-sdk-integration`) con propiedad `AWS_S3_ENDPOINT` (`http://localhost:14566` en LocalStack / estándar en AWS) para persistir PDFs reales en bucket.
- [ ] **4. *(Cuando el emulador esté validado)* Despliegue real en AWS Free Tier:** Seguir la Parte 2 de `aws Tutorial.md` (Budgets, Security Groups, RDS PostgreSQL privado, S3 privado y EC2 con Nginx + `systemd`).

## Comandos clave (terminal integrada de VS Code)
```
# Backend
mvn21.cmd clean compile   # compilar
mvn21.cmd test            # tests (49/49 verdes)
mvn21.cmd spring-boot:run # ejecutar backend en http://localhost:8080
mvn21.cmd spring-boot:run -Dspring-boot.run.profiles=local # ejecutar backend con datos semilla de demo

# Frontend
cd frontend
npm start                 # ng serve en http://localhost:4200
npm test                  # ng test (Karma/Jasmine)
node_modules/.bin/tsc --noEmit -p tsconfig.app.json   # type-check

# Git
git status --short --branch
git log --oneline -5
```

## Reglas del proyecto (no romper)
- Soft-delete obligatorio: nunca `deleteById` físico en pacientes.
- Spec primero: cualquier endpoint nuevo va antes en `docs/openapi.yaml`, luego en Java.
- Todo cambio debe tener test que pase.
- No tocar `opencode.json` (cambio LF/CRLF ajeno) ni `spring-petclinic/` (subproyecto Java 17 + Gradle, sin permiso).
- CORS: cuando el frontend corra en :4200 y el backend en :8080, el backend debe aceptar peticiones desde `http://localhost:4200`.
