# TODO — Nutricional Coach (MVP)

## Hasta el MVP

### Backend (HECHO, verificado)
- [x] `GET /api/v1/pacientes` — listar solo activos, paginación page/size (suite 39/39 verdes)
- [x] `GET /api/v1/pacientes/{id}` — obtener paciente por ID (200/404)
- [x] `POST /api/v1/pacientes` — alta de paciente (201 Created + Location, 400 sin nombre/email)
- [x] `DELETE /api/v1/pacientes/{id}` — soft-delete (activo=false + fechaBaja, 204/404)
- [x] `POST /api/v1/pacientes/{id}/mediciones` — registrar medición (201 Created + Location)
- [x] `GET /api/v1/pacientes/{id}/mediciones` — listar mediciones de un paciente (200)
- [x] `GET /api/v1/revisiones` y `GET /api/v1/revisiones/{id}` — revisiones mensuales (200/404)
- [x] `POST /api/v1/pacientes/{id}/revisiones` — crear revisión con deltas (201 Created + Location)
- [x] `GET /api/v1/revisiones/{id}/pdf` — descargar PDF de plan de dieta/entreno (OpenPDF, 200/404)
- [x] `GET /api/v1/recordatorios` — listar recordatorios pendientes (200 con lista RecordatorioDTO)
- [x] `PATCH /api/v1/recordatorios/{id}` — actualizar feedback del paciente (200/404)

### Frontend Angular 18 (`npm run dev`) — EN PROGRESO
- [x] T1: Generar proyecto Angular 18 (carpeta `frontend/`, routing, CSS, npm)
- [x] T2: Servicio HTTP base `http://localhost:8080/api/v1` (`core/api.config.ts`, `core/api.service.ts`, `provideHttpClient()` en `app.config.ts`)
- [x] T3: Páginas de pacientes (alta S1, listar S2, detalle S3)
- [ ] T4: Soft-delete de paciente (S4)
- [x] T5: Mediciones biométricas por paciente (S5, S6)
- [ ] T6: Revisiones mensuales (listar S7, crear S8, descargar PDF S9)
- [ ] T7: Recordatorios pendientes + actualizar feedback (S10, S11)
- [ ] T8: Tests que pinchen cada endpoint (200/201/404) (S1-S11)
- [ ] T9: Documentar dificultades y comandos en `docs/CHANGELOG.md` (S1-S11)

### Infra / Entorno
- [ ] `.env` con `DB_URL`, `DB_USER`, `DB_PASSWORD`, `AWS_REGION`, credenciales S3 (RDS/S3)

### Release (BLOQUEADO)
- [ ] PR de `feat/recordatorios` a `main` — `gh` no autenticado. Rama ya pusheada (5 commits: 171b197, e7867a0, 61ac33a, b958430, 8feebdf). Requiere `gh auth login` o crear PR manualmente en GitHub.

## Comandos clave (terminal integrada de VS Code)
```
# Backend
mvn21.cmd clean compile   # compilar
mvn21.cmd test            # tests (39/39 verdes)
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
