# Estado actual del proyecto — Nutricional Coach (ODD)

## Objetivo
Documentar el estado actual del proyecto y establecer el punto de partida para la siguiente feature.

Branch: `main` (rama activa: `feat/soft-delete-mediciones-biometricas`)
Delivery strategy: `ask-on-risk` (default)
Test runner: `mvn21.cmd test` (proyecto principal), `./gradlew test` (spring-petclinic)

## Specs

S1: "Soft-delete obligatorio: nunca `deleteById` físico en pacientes" — ver `docs/constitution.md` §6, `PacienteController.java`, `odd/tasks/delete-paciente.md` S3.
S2: "Spec primero: cualquier endpoint nuevo va antes en `docs/openapi.yaml`, luego en Java" — ver `docs/constitution.md` §3.
S3: "Todo cambio debe tener test que pase" — ver `docs/constitution.md` §4, `PLAN_PENDIENTE_MVP.md`.

## Tasks

| ID | Linked S# | Route | Status | Commit |
|----|-----------|-------|--------|--------|
| T1 | S1 | inline | done | `7513c36` feat(pacientes): listar pacientes activos con GET /api/v1/pacientes |
| T2 | S1, S2 | inline | done | `29161e0` Dia 8 - T2: GET /api/v1/pacientes/{id}/mediciones |
| T3 | S3 | inline | pending | Documentar inmutabilidad en `docs/CHANGELOG.md` |

## Log

L1: [usuario] "Aplicar estructura de documentación ODD simplificada en Nutricional Coach, igual que en NutriControl-API"
L2: [asistente] Creado `docs/organizacion-documentacion.md` (mapa de documentación y porqué), `odd/task/estado-actual-proyecto.md` (único archivo de definición de estado actual ODD), y `todo` (checklist de sesión). Se mantuvo `DEV_RUNBOOK.md`, `TODO.md` y `PLAN_PENDIENTE_MVP.md` existentes; no se eliminaron documentos históricos.