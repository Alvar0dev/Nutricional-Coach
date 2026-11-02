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