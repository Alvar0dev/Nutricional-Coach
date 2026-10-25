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
| T3 | S1, S2, S3 | Implementación `PacienteServiceImpl.softDeletePaciente()` (activo=false + fechaBaja) | Hecho |
| T4 | S1, S2, S3 | Repositorio `PacienteRepository` (JPA) | Hecho |
| T5 | S1-S4 | Test `PacienteServiceImplTest.java` (soft-delete existente / inexistente) | Hecho |
| T6 | S4 | Test `PacienteControllerTest.java` (GET filtrado por activo=true) | Hecho — requiere `GET /api/v1/pacientes` implementado (ver L4) |

## Log

- **L1** (2026-10-22): Usuario solicita implementar `DELETE` como avance del día 4. Confirmado que es cambio pequeño (1 archivo + 1 test). Riesgo: item 1 (datos irreversibles). Mitigación: soft-delete con `activo=false` y `fechaBaja`.
- **L2** (2026-10-22): Se confirma que no hay conflicto con rebase; repositorio limpio (`git status` OK).
- **L3** (2026-10-23): Día actual — se implementa `PacienteServiceImpl` con soft-delete obligatorio, `PacienteRepository`, y test `PacienteServiceImplTest` cubriendo paciente existente (marca activo=false, fechaBaja, guarda) e inexistente (retorna false, no guarda). Se agrega `pom.xml` mínimo para build verificable. Se agregan modelos `Paciente` y DTOs alineados con `openapi.yaml`.
- **L4** (2026-10-25): Entorno desbloqueado (Maven 3.9.9 + Java 21 Eclipse Adoptium; wrapper `mvn21.cmd`). Arreglados 2 tests preexistentes de `MedicionBiometricaControllerTest` (matcher en `verify`, contexto de servlet para `fromCurrentRequest`). Implementado `GET /api/v1/pacientes` (`listarPacientes`, filtro `activo=true` vía `PacienteRepository.findByActivoTrue()`, `PacienteService.listarPacientesActivos()`, paginación `page`/`size`) con 3 tests nuevos (service + 2 controller cubriendo S4). Suite verde: 13/13. Risk: none (endpoint nuevo, sin tocar DELETE ni helpers compartidos).

## Risk
Risk: item 1 (datos irreversibles). Mitigación: soft-delete obligatorio con `activo=false` y `fechaBaja`; sin eliminación física permitida. T6 pendiente requiere endpoint GET pacientes filtrado por activo=true.
