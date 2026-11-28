# PROJECT_INDEX.md — Mapa de navegación arquitectónico

## Propósito
Plataforma cloud de nutrición deportiva y biometría. Flujo: alta paciente → medición biométrica → alertas → revisión mensual → PDF (dieta/entreno). Metodología ODD (Organic Driven Development).

## Stack principal
- **Runtime:** Java 21 (`C:\Users\alvar\bin\mvn21.cmd` / `jdk-21.0.5.11-hotspot` para proyecto principal) / Java 17 (subproyecto `spring-petclinic/`)
- **Backend:** Spring Boot `3.4.0` (`pom.xml`), Hibernate JPA, Spring Web, OpenPDF `1.3.27`
- **Base de datos:** H2 en memoria (perfil por defecto / `local` demo) y PostgreSQL (driver `runtime` + perfil `prod` para RDS / emulador Docker)
- **API:** OpenAPI 3.0 (`docs/openapi.yaml` — fuente de verdad de contratos)
- **Frontend:** Angular 18 (`frontend/`, `API_BASE_URL = '/api/v1'`, `proxy.conf.json` en dev y Nginx en prod)
- **Almacenamiento PDF:** `PdfStorageService` (`LocalPdfStorageService` por defecto; `S3PdfStorageService` condicional con `aws.s3.enabled=true`)
- **Cloud / Emulador:** AWS Free Tier (EC2 + RDS + S3, Parte 2 de `aws Tutorial.md`) y emulador Docker local $0 (`C:\lab-aws-nutricional\`, Parte 1 de `aws Tutorial.md`)
- **Tests:** JUnit 5 + Mockito + `MockMvc` + `@SpringBootTest` (backend) y Jasmine/Karma en `ChromeHeadless` (frontend)

## Estructura de carpetas (responsabilidad)

- `docs/` — Contratos y trazabilidad (`openapi.yaml`, `CHANGELOG.md`, `constitution.md`, `organizacion-documentacion.md`).
- `odd/` — Estado base ODD (`odd/task/estado-actual-proyecto.md`) y tracking de features (`odd/tasks/`).
- `src/main/java/com/nutricionalcoach/` — Backend Spring Boot (`config/`, `controller/`, `dto/`, `model/`, `repository/`, `service/`, `NutricionalCoachApplication.java`).
- `src/main/resources/` — `application.properties` (H2/fallback + placeholders `.env`) y `application-prod.properties` (PostgreSQL RDS + `h2.console.enabled=false`).
- `src/test/java/com/nutricionalcoach/` — Tests unitarios, `MockMvc` e integración (`NutricionalCoachApplicationTest`, `DataInitializerTest`, `ProdProfileConfigTest`, `PdfStorageServiceTest`, controllers y services).
- `frontend/` — Aplicación Angular 18 (`src/app/core/api.config.ts`, servicios, páginas, specs).
- `spring-petclinic/` — Subproyecto independiente (gitlink, Java 17 + Gradle). **No modificar sin permiso.**
- `AGENTS.md`, `DEV_RUNBOOK.md`, `TODO.md`, `todo`, `plan.md`, `aws Tutorial.md` — Contexto operativo, comandos, checklist y guías de despliegue.

## Puntos de entrada (entry points)

- **Arranque Backend:** `src/main/java/com/nutricionalcoach/NutricionalCoachApplication.java` (`DataInitializer.java` bajo `@Profile("local")`)
- **Controladores REST (`@RequestMapping` bajo `/api/v1` + CORS `:4200`):**
  - `src/main/java/com/nutricionalcoach/controller/PacienteController.java`
  - `src/main/java/com/nutricionalcoach/controller/MedicionBiometricaController.java`
  - `src/main/java/com/nutricionalcoach/controller/RevisionController.java`
  - `src/main/java/com/nutricionalcoach/controller/RecordatorioController.java`
- **Almacenamiento PDF:** `PdfStorageService.java`, `LocalPdfStorageService.java`, `S3PdfStorageService.java` (inyectados en `RevisionServiceImpl.java`)
- **Frontend SPA:** `frontend/src/app/core/api.config.ts` (`API_BASE_URL = '/api/v1'`) y build estático en `frontend/dist/frontend/browser/`

## Contratos clave (rutas/firmas representativas)

- `DELETE /api/v1/pacientes/{id}` → `PacienteController.deletePaciente` → `PacienteService.softDeletePaciente` → `204` / `404`
- `GET /api/v1/pacientes` → `PacienteController.listarPacientes` → `PacienteService.listarPacientesActivos` → `200` (paginado)
- `GET /api/v1/pacientes/{id}` → `PacienteController.obtenerPacientePorId` → `200` (`PacienteDTO`) / `404`
- `POST /api/v1/pacientes` → `PacienteController.crearPaciente` → `201 Created` + `Location` / `400` sin nombre o email
- `POST /api/v1/pacientes/{id}/mediciones` → `MedicionBiometricaController.registrarMedicion` → `201 Created` + `Location`
- `GET /api/v1/pacientes/{id}/mediciones` → `MedicionBiometricaController.listarMediciones` → `200`
- `GET /api/v1/revisiones` y `GET /api/v1/revisiones/{id}` → `RevisionController` → `200` / `404`
- `POST /api/v1/pacientes/{id}/revisiones` → `RevisionController.crearRevision` → `201 Created` + `Location`
- `GET /api/v1/revisiones/{id}/pdf` → `RevisionController.descargarPdf` → `200 application/pdf` / `404`
- `GET /api/v1/recordatorios` → `RecordatorioController.listarRecordatoriosPendientes` → `200` (`RecordatorioDTO[]`)
- `PATCH /api/v1/recordatorios/{id}` → `RecordatorioController.actualizarFeedback` → `200` / `404`

## Flujo de datos crítico (request → persistencia)

```text
Client (Angular :4200 en dev / Nginx :80 en prod)
  → /api/v1/* (REST Controllers)
  → Service Layer (*ServiceImpl + PdfStorageService)
  → Spring Data JPA Repositories
  → DB (H2 en memoria local / PostgreSQL RDS en prod)
  → Response DTO / OpenPDF byte[]
```

## Reglas de negocio críticas (referenciadas por archivo, no duplicadas)

- **Soft-delete obligatorio:** ver `docs/constitution.md` §6, `PacienteController.java`, `Paciente.softDelete()`.
- **Alineación spec-código:** ver `docs/constitution.md` §3, `AGENTS.md` apartado límites, `docs/openapi.yaml`.
- **Separación lógica-interfaz:** ver `docs/constitution.md` §2, `AGENTS.md`.
- **Testing obligatorio:** ver `docs/constitution.md` §4 y `DEV_RUNBOOK.md`.

## Estado actual (2026-11-28)
- **Rama activa:** `main` (PR #1–#6 mergeados en GitHub).
- **MVP + Empaquetado:** Implementados y verificados en Día 23 (`49/49` tests backend con `mvn21.cmd clean package` y `15/15` tests frontend Angular 18 con `tsc` y `ng build` limpios).
- **Emulador AWS Local ($0):** Validado E2E en `prod` (`PROD_JPA_DDL_AUTO=update` → `validate` sobre PostgreSQL 16 + Nginx `:80` + LocalStack S3 `4.14.0` en `14566:4566`).
- **Próximos pasos:** ver `plan.md` y `TODO.md` (`TODO 2` opcional SDK real de AWS S3 en `feat/s3-sdk-integration`, y `TODO 4` despliegue real en AWS Free Tier).
