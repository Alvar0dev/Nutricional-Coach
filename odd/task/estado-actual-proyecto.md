# Estado actual del proyecto — Nutricional Coach (ODD)

## Objetivo
Único archivo de definición de estado base ODD del proyecto y punto de partida para nuevas sesiones.

Branch: `feat/s3-sdk-integration` (desde `main` `1340009`; PR #1–#6 mergeados, TODO 2 listo para PR #7)
Delivery strategy: `ask-on-risk` (default)
Test runner:
- Backend: `C:\Users\alvar\bin\mvn21.cmd test` (`51/51` verdes verificados hoy + verificador independiente `PASS`)
- Frontend: `npm test -- --watch=false --browsers=ChromeHeadless` en `frontend/` (`15/15` verdes verificados en Día 23)
- Subproyecto independiente: `./gradlew test` en `spring-petclinic/` (no tocar sin permiso)

## Specs (invariantes)

S1: "Soft-delete obligatorio: nunca `deleteById` físico en pacientes" — ver `docs/constitution.md` §6, `PacienteController.java`, `odd/tasks/delete-paciente.md` S3.
S2: "Spec primero: cualquier endpoint nuevo va antes en `docs/openapi.yaml`, luego en Java" — ver `docs/constitution.md` §3.
S3: "Todo cambio debe tener test que pase" — ver `docs/constitution.md` §4, `DEV_RUNBOOK.md`.

## Resumen de bloques completados

| Bloque | Alcance | Estado | Evidencia / Ref |
|---|---|---|---|
| MVP Backend + Frontend | Endpoints CRUD/PDF/recordatorios + Angular 18 | done | PR #1 (`462393a`) |
| Entrypoint Spring Boot + H2 + `.env` + `local` seed | Arranque vivo `:8080`, `DataInitializer` y `.env.example` | done | PR #2 (`033bb28`) |
| Perfil `prod` PostgreSQL RDS | Driver PostgreSQL + `application-prod.properties` | done | PR #3 (`897831f`) |
| `PdfStorageService` (Local / S3 condicional) | Puerto salida PDF en `RevisionServiceImpl` | done | PR #4 (`197983d`) |
| Empaquetado prod + Guía AWS | `API_BASE_URL='/api/v1'`, JAR + `dist/`, `aws Tutorial.md` | done | PR #5 (`e982e88`), PR #6 (`9e4b203`) |
| Validación E2E emulador local ($0) + `plan.md` | Docker Postgres/Nginx/LocalStack `4.14.0` + `prod` `validate` | done | Día 27/28 (`docs/CHANGELOG.md`, `1340009`) |
| Integración SDK real AWS S3 (`software.amazon.awssdk:s3`) | `S3ClientConfig` + `S3PdfStorageService` + LocalStack S3 vivo + `odd-verifier` PASS | done (pendiente PR #7) | `feat/s3-sdk-integration` (`51/51`) |
| Próximo paso (`TODO 4` AWS Free Tier) | Despliegue real en EC2 + RDS + S3 siguiendo Parte 2 de `aws Tutorial.md` | pending | `plan.md`, `TODO.md` |

## Log

L1: [usuario] "Aplicar estructura de documentación ODD simplificada en Nutricional Coach, igual que en NutriControl-API"
L2: [asistente] Creado `docs/organizacion-documentacion.md`, `odd/task/estado-actual-proyecto.md` y `todo`.
L3: [asistente] 2026-11-28 (Paso 0 / TODO 3): Alineado `odd/task/estado-actual-proyecto.md` y `PROJECT_INDEX.md` al estado `main` post-MVP tras validar E2E el emulador AWS local (`prod` `update` → `validate`) y crear `plan.md`.
L4: [asistente] 2026-11-28 (TODO 2): Integrado AWS SDK v2 (`software.amazon.awssdk:s3` `2.31.78`) en rama `feat/s3-sdk-integration` con `S3ClientConfig`, `S3PdfStorageService` real, `51/51` tests verdes, subida/lectura real en LocalStack S3 (`1079` bytes) y verificación independiente `PASS` (`Risk: item 5`).
