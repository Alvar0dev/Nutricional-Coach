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