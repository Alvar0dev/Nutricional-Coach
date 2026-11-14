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

### Hecho hoy (Día 19 — 2026-11-14)
- [x] **1. Dependencia H2, punto de entrada `@SpringBootApplication` y `application.properties`:** Agregado `com.h2database:h2` a `pom.xml`, creada `com.nutricionalcoach.NutricionalCoachApplication`, configurado `src/main/resources/application.properties` (`jdbc:h2:mem:nutricionaldb`, `/h2-console`, `ddl-auto=update`), anotadas las 4 entidades JPA (`@Entity`, `@Table`, `@Id`), ajustado `RevisionController.crearRevision` para propagar `@PathVariable id` al DTO cuando no viene en el body, y creado `NutricionalCoachApplicationTest` (suite **41/41 tests verdes**).
- [x] **2. Prueba E2E en vivo (`:4200` ↔ `:8080`):** Levantados backend (`mvn21.cmd spring-boot:run` en `:8080`) y frontend (`npm start` en `:4200`), validando en vivo H2 Console (`200`), alta y listado de pacientes (`201`/`200` + CORS `:4200`), registro y consulta de mediciones (`201`/`200`), creación de revisión mensual y descarga de PDF OpenPDF (`201`/`200 application/pdf`, `1115 bytes`), recordatorios (`200`) y baja lógica soft-delete (`204`).

### Pendientes (Infraestructura / Cloud)
- [ ] **3. Infra / Entorno (`.env`):** Crear `.env` y plantilla `.env.example` con `DB_URL`, `DB_USER`, `DB_PASSWORD`, `AWS_REGION`, credenciales S3 (RDS/S3).

## Comandos clave (terminal integrada de VS Code)
```
# Backend
mvn21.cmd clean compile   # compilar
mvn21.cmd test            # tests (41/41 verdes)
mvn21.cmd spring-boot:run # ejecutar backend en http://localhost:8080

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
