# Feature: POST /api/v1/pacientes/{id}/mediciones

Referencia: `docs/openapi.yaml` (endpoint `recordBiometricMeasurement`)
Especificación: `docs/constitution.md` §3 (alineación spec-código)

## Specs (S#)

- **S1**: `POST /api/v1/pacientes/{id}/mediciones` debe registrar una medición biométrica del paciente (UUID en path). El cuerpo debe incluir `fecha`, `pesoKg`, `grasaPct`, `masaMuscularKg`, `cinturaCm`.
- **S2**: El endpoint debe validar que `pesoKg` y `grasaPct` no sean negativos (`min: 0` en schema `MedicionBiometricaDTO`).
- **S3**: El registro debe persistirse con `fecha` actual (o la proporcionada por el cliente si explícita) y debe ser trazable con `pacienteId`.
- **S4**: La respuesta debe ser `201 Created` con referencia al recurso creado.
- **S5**: Cada medición debe ser inmutable una vez creada (sin `PUT` ni `DELETE` en la spec) — riesgo 1 mitigado por inmutabilidad.

## Tareas

| ID | Spec | Ruta | Estado |
|----|------|------|--------|
| T1 | S1, S2, S3 | Implementar `recordBiometricMeasurement` en `MedicionBiometricaController.java` | Hecho |
| T2 | S4 | Añadir `GET /api/v1/pacientes/{id}/mediciones` para listar (ya existe en spec; pendiente controller) | Pendiente |
| T3 | S5 | Documentar inmutabilidad de mediciones en `docs/CHANGELOG.md` | Hecho (referencia en spec) |
| T4 | S1, S2, S3 | Implementar `MedicionBiometricaService.registrarMedicion()` con validación pesoKg/grasaPct >= 0 | Hecho |
| T5 | S1, S2, S3 | Repositorio `MedicionBiometricaRepository` (JPA) | Hecho |
| T6 | S1-S5 | Modelos `MedicionBiometrica` y DTO `MedicionBiometricaDTO` | Hecho |
| T7 | S1-S5 | Test `MedicionBiometricaServiceImplTest.java` (registro válido, paciente inexistente, peso negativo, grasa negativa) | Hecho |
| T8 | S4 | Test `MedicionBiometricaControllerTest.java` (respuesta 201, Location header, excepción propagada) | Hecho |

## Log

- **L1** (2026-10-22): Usuario confirma dejar workspace artifacts untracked (`.config/`, `.gemini/`, `.at/`). No afectan el flujo ODD.
- **L2** (2026-10-18): Creación de feature doc como parte del flujo ODD día 5 (post-`DELETE` endpoint). Cada feature debe apuntar a sus `S#` en la spec OpenAPI.
- **L3** (2026-10-22): Riesgo: item 1 (datos biométricos irreversibles). Mitigación: inmutabilidad del registro (no permite modificación ni borrado físico) + referencia al `PacienteService` para trazabilidad.
- **L4** (2026-10-23): Día actual — se implementa `MedicionBiometricaController` con POST `/api/v1/pacientes/{id}/mediciones` respondiendo `201 Created` y `Location` header. Se implementa `MedicionBiometricaServiceImpl` con validación de `pesoKg` y `grasaPct` no negativos, trazabilidad con `pacienteId`, y fecha por defecto actual. Se agregan `MedicionBiometricaRepository`, modelos y DTOs. Se agregan tests de servicio y controlador.

## Risk
Risk: item 1 (datos biométricos irreversibles). Mitigación: registros inmutables (`POST` sin `PUT`/`DELETE` en spec); trazabilidad obligatoria con `pacienteId`; validación `min: 0` para evitar datos corruptos; `GET` para listar pendiente (T2).
