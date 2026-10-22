# Feature: DELETE /api/v1/pacientes/{id}

Referencia: `docs/openapi.yaml` (endpoint `deletePatient`)
Especificación: `AGENTS.md` (límites de edición confirmados)

## Specs (S#)

- **S1**: `DELETE /api/v1/pacientes/{id}` debe eliminar (soft-delete) un paciente existente, identificado por UUID en el path. No debe borrar registros relacionados de mediciones ni revisiones.
- **S2**: El endpoint debe responder `204 No Content` si la eliminación fue exitosa, o `404 Not Found` si el paciente no existe.
- **S3**: La eliminación debe ser reversible (soft-delete): marcar `fechaBaja` y `activo=false` en lugar de eliminar físicamente (protección de datos — riesgo 1 de la Constitución).
- **S4**: El cambio debe incluir un test que verifique que un paciente eliminado no aparece en `GET /api/v1/pacientes` (filtrado por `activo=true`).

## Tareas

| ID | Spec | Ruta | Estado |
|----|------|------|--------|
| T1 | S1, S2, S3 | Creación del endpoint `deletePatient` en `PacienteController.java` | Hecho |
| T2 | S4 | Test `PacienteControllerTest.java` para validación de soft-delete | Hecho |

## Log

- **L1** (2026-10-22): Usuario solicita implementar `DELETE` como avance del día 4. Confirmado que es cambio pequeño (1 archivo + 1 test). Riesgo: item 1 (datos irreversibles). Mitigación: soft-delete con `activo=false` y `fechaBaja`.
- **L2** (2026-10-22): Se confirma que no hay conflicto con rebase; repositorio limpio (`git status` OK).

## Risk
Risk: item 1 (datos irreversibles). Mitigación: soft-delete obligatorio. Sin `activo=true` en la eliminación física no se permite commit.
