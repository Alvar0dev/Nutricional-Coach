## Día 8 — 2026-10-26: T2 mediciones biométricas GET /pacientes/{id}/mediciones

## Logro
- Implementado `GET /api/v1/pacientes/{id}/mediciones` para listar mediciones de un paciente (200 con lista de MedicionBiometricaDTO, 404 si el paciente no existe).
- Implementado también `POST /api/v1/pacientes/{id}/mediciones` para registrar una nueva medición (201 Created con Location header).
- `MedicionBiometricaController`, `MedicionBiometricaService`, `MedicionBiometricaServiceImpl`, `MedicionBiometricaRepository`, `MedicionBiometricaDTO`, `MedicionBiometrica` model.
- Tests: 4 nuevos en `MedicionBiometricaControllerTest` (listar 200, listar 404, registrar 201, registrar paciente inexistente) y 6 nuevos en `MedicionBiometricaServiceImplTest`.
- Suite: 17/17 verdes, BUILD SUCCESS.

## Dificultad 1
**Problema:** Maven no recompila cambios en archivos modificados (`Nothing to compile - all classes are up to date`).  
**Solución:** `mvn clean test` para forzar recompilación completa desde cero.  

**Por qué:** Maven a veces reutiliza clases target sin detectar cambios en archivos fuente.

## Dificultad 2
**Problema:** Variable `service` no declarada en tests del controller (`MedicionBiometricaControllerTest`).  
**Solución:** Declarar `MedicionBiometricaService service = mock(MedicionBiometricaService.class);` al inicio de cada test, siguiendo el patrón existente.  

**Por qué:** Mantiene consistencia con los tests originales y evita acoplamiento entre tests.

## Riesgo
Ninguno nuevo: endpoint de consulta es de solo lectura; las mediciones son inmutables (no hay PUT ni DELETE en la especificación).

## Día 11 — 2026-11-02: GET /api/v1/revisiones y GET /api/v1/revisiones/{id}

## Logro
- Implementado `GET /api/v1/revisiones` para listar todas las revisiones mensuales (200 con lista de RevisionDTO).
- Implementado `GET /api/v1/revisiones/{id}` para obtener una revisión por ID (200 con RevisionDTO, 404 si no existe).
- Implementado también `POST /api/v1/pacientes/{id}/revisiones` para crear una revisión mensual con deltas calculados (201 Created con Location header).
- `Revision` model, `RevisionDTO`, `RevisionRepository`, `RevisionService`, `RevisionServiceImpl`, `RevisionController`.
- Tests: 4 nuevos en `RevisionControllerTest` (listar 200, obtener 200, obtener 404, crear 201) y 6 nuevos en `RevisionServiceImplTest`.
- Suite: 32/32 verdes, BUILD SUCCESS (era 22, +10 nuevos).

## Dificultad
Ninguna: se siguió el patrón establecido de mediciones biométricas (model → DTO → repository → service → controller → tests).

## Riesgo
Ninguno nuevo: endpoints de consulta son de solo lectura; la creación valida que el pacienteId no esté vacío.

# T3: Documentar inmutabilidad de mediciones en docs/CHANGELOG.md

## Logro
- Implementado `GET /api/v1/pacientes/{id}/mediciones` (200/404), 4 tests nuevos, suite 17/17 verdes (BUILD SUCCESS).

## Dificultad 1
**Problema:** Maven no recompila cambios en archivos modificados (`Nothing to compile - all classes are up to date`).  
**Solución:** `mvn clean test` para forzar recompilación completa desde cero.  

**Por qué:** Maven a veces reutiliza clases target sin detectar cambios en archivos fuente.

## Dificultad 2
**Problema:** Variable `service` no declarada en tests del controller (`MedicionBiometricaControllerTest`).  
**Solución:** Declarar `MedicionBiometricaService service = mock(MedicionBiometricaService.class);` al inicio de cada test, siguiendo el patrón existente.  

**Por qué:** Mantiene consistencia con los tests originales y evita acoplamiento entre tests.

## Día 9 — 2026-10-27: POST /api/v1/pacientes (alta de paciente)

## Logro
- Implementado `POST /api/v1/pacientes` para crear un paciente (201 Created con `Location` header, id y `fechaAlta` generados automáticamente; 400 si falta nombre o email).
- Agregados 3 tests nuevos en `PacienteControllerTest` (creación válida 201, 400 sin nombre, 400 sin email).
- Suite: 20/20 verdes, BUILD SUCCESS, sin regresiones.

## Dificultad
**Problema:** El proyecto no tiene `spring-boot-starter-validation`, por no se puede usar `@Valid`/`@NotBlank` sobre el DTO.  
**Solución:** Validación manual con `isBlank()` en controller y service, sin añadir dependencias.  

**Por qué:** Mantener el stack minimal y evitar dependencias nuevas no solicitadas.

## Riesgo
Ninguno nuevo: endpoint de creación no modifica datos existentes; validación evita registros con nombre/email vacíos.

## Día 10 — 2026-11-01: GET /api/v1/pacientes/{id} y T3 inmutabilidad

## Logro
- Implementado `GET /api/v1/pacientes/{id}` (200 con PacienteDTO, 404 si no existe) en `PacienteController`, `PacienteService.obtenerPacientePorId` y `PacienteServiceImpl`.
- Agregados 2 tests nuevos en `PacienteControllerTest` (200 con id existente, 404 id inexistente).
- Suite: 22/22 verdes, BUILD SUCCESS, sin regresiones (era 20, +2 nuevos).

## T3 — Documentar inmutabilidad de pacientes y mediciones
- `Paciente` usa **soft-delete**: `activo=false` + `fechaBaja` en lugar de eliminación física (`Paciente.softDelete()`).
- `PacienteService.softDeletePaciente` marca inactivo y guarda; nunca elimina registros físicos del repositorio.
- `GET /api/v1/pacientes` lista solo pacientes activos (`findByActivoTrue`); dados de baja no aparecen.
- Las mediciones y revisiones relacionadas conservan su trazabilidad al no borrarse físicamente el paciente al que pertenecen.

## Dificultad
**Problema:** `mvn` no está en el PATH del sistema.  
**Solución:** Usar `/c/Users/alvar/bin/mvn21.cmd` que fija `JAVA_HOME` al JDK 21 y llama a Maven 3.9.9.  

**Por qué:** JAVA_HOME del sistema apuntaba a JDK 17 y fallaba la compilación.

## Riesgo
Ninguno nuevo: endpoint de consulta es de solo lectura; soft-delete protege datos existentes de eliminación irreversible.

## Día 13 — 2026-11-03: PATCH /api/v1/recordatorios/{id} (actualizar feedback del paciente)

## Logro
- Implementado `PATCH /api/v1/recordatorios/{id}` para actualizar el feedback del paciente de un recordatorio existente (200 con RecordatorioDTO actualizado, 404 Not Found si no existe).
- Endpoint alineado con `docs/openapi.yaml` (operación `updateReminderFeedback`, schema `RecordatorioFeedbackDTO` con solo `feedbackPaciente`).
- Archivos nuevos/modificados:
  - `src/main/java/com/nutricionalcoach/dto/RecordatorioFeedbackDTO.java` (nuevo: DTO con solo `feedbackPaciente`)
  - `src/main/java/com/nutricionalcoach/service/RecordatorioService.java` (+`actualizarFeedback(id, feedback)` → `Optional<RecordatorioDTO>`)
  - `src/main/java/com/nutricionalcoach/service/RecordatorioServiceImpl.java` (+implementación de `actualizarFeedback`, `Optional.empty()` si no existe)
  - `src/main/java/com/nutricionalcoach/controller/RecordatorioController.java` (+`PATCH /{id}` → 200/404)
  - `src/test/java/com/nutricionalcoach/controller/RecordatorioControllerTest.java` (+2 tests: PATCH 200 feedback actualizado, PATCH 404 no existe)
- Tests: 2 nuevos en `RecordatorioControllerTest`.
- Suite: **35/35 verdes**, `BUILD SUCCESS`, sin regresiones (era 33, +2 nuevos).

## Dificultad
Ninguna: se siguió exactamente el patrón establecido de recordatorios (DTO → service → controller → tests). El contrato ya existía en `openapi.yaml`.

## Riesgo
Ninguno nuevo: endpoint de actualización aislado sobre recordatorios, sin tocar código compartido ni otros endpoints; todos los casos cubiertos con test.

## Día 12 — 2026-11-02: GET /api/v1/recordatorios (listar pendientes)

## Logro
- Implementado `GET /api/v1/recordatorios` para listar recordatorios con estado `PENDIENTE` (200 con lista de `RecordatorioDTO`, vacía si no hay ninguno).
- Endpoint alineado con `docs/openapi.yaml` (schemas `RecordatorioDTO` y operación `listPendingReminders`).
- Archivos creados:
  - `src/main/java/com/nutricionalcoach/model/Recordatorio.java` (entidad: id, pacienteId, fechaProgramada, tipo, estado, feedbackPaciente; estado por defecto `PENDIENTE`)
  - `src/main/java/com/nutricionalcoach/dto/RecordatorioDTO.java` (DTO alineado al schema OpenAPI)
  - `src/main/java/com/nutricionalcoach/repository/RecordatorioRepository.java` (`JpaRepository` con `findByEstado` y `findAll`)
  - `src/main/java/com/nutricionalcoach/service/RecordatorioService.java` + `RecordatorioServiceImpl.java` (`listarRecordatoriosPendientes()`)
  - `src/main/java/com/nutricionalcoach/controller/RecordatorioController.java` (`GET /api/v1/recordatorios` → 200 OK)
  - `src/test/java/com/nutricionalcoach/controller/RecordatorioControllerTest.java` (test: 200 con lista de 2 recordatorios pendientes, verifica ids y estados)
- Tests: 1 nuevo en `RecordatorioControllerTest`.
- Suite: **33/33 verdes**, `BUILD SUCCESS`, sin regresiones (era 32, +1 nuevo).
- Commit: `171b197` en rama `feat/recordatorios`.

## Dificultad
Ninguna: se siguió exactamente el patrón establecido de revisiones mensuales (modelo → DTO → repository → service → controller → tests). El contrato ya existía en `openapi.yaml`, por lo que solo hubo que traducirlo a Java sin decisiones de diseño abiertas.

## Por qué se hizo antes que otros pendientes
- Cierra **todos** los endpoints de consulta definidos en `openapi.yaml` (pacientes, mediciones, revisiones y recordatorios).
- Los recordatorios son el paso 3 del flujo de negocio del README (alta paciente → medición → alertas/recordatorios → revisión mensual → PDF).
- Es una tarea pequeña (understood, contained risk, resumable desde el diff), por lo que se implementó inline sin documento de feature grande.

## Riesgo
Ninguno nuevo: endpoint de consulta de solo lectura; no modifica ni elimina datos; el estado `PENDIENTE` se asigna por defecto en la entidad y nunca se expone como editable desde este endpoint.