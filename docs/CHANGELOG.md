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

## Día 13 — 2026-11-04: GET /api/v1/revisiones/{id}/pdf (OpenPDF)

## Logro
- Implementado `GET /api/v1/revisiones/{id}/pdf` para descargar el PDF del plan de dieta y entreno de una revisión mensual (200 con `application/pdf`, 404 si la revisión no existe).
- Dependencia agregada: `com.github.librepdf:openpdf:1.3.27` en `pom.xml`.
- `RevisionService.generarPdfRevision(String id)` → genera el PDF con OpenPDF o devuelve `null` si la revisión no existe.
- `RevisionController.descargarPdf(String id)` → endpoint `GET /{id}/pdf` con `Content-Type: application/pdf` y `Content-Disposition: attachment`.
- Tests: 2 nuevos en `RevisionControllerTest` (PDF 200 con content-type y disposition, 404 si no existe) y 2 nuevos en `RevisionServiceImplTest` (PDF genera bytes, inexistente devuelve null).
- Suite: **39/39 verdes**, `BUILD SUCCESS` (era 35, +4 nuevos), sin regresiones.

## Dificultad
Ninguna: se siguió el patrón establecido de revisiones mensuales. El contrato ya existía en `docs/openapi.yaml` (operación `downloadRevisionPDF`), solo hubo que traducirlo a Java.

## Riesgo
Ninguno nuevo: endpoint de consulta de solo lectura; genera un archivo PDF descargable sin modificar datos persistidos.

## Día 15 — 2026-11-07: Frontend T5 mediciones biométricas por paciente (S5, S6)

## Logro
- Implementado T5 del frontend Angular 18 en rama `feat/recordatorios`: páginas de mediciones biométricas por paciente para registro (S5, POST /pacientes/{id}/mediciones) y listado (S6, GET /pacientes/{id}/mediciones), conectadas a los endpoints del backend en `http://localhost:8080/api/v1`.
- Archivos nuevos/modificados:
  - `frontend/src/app/core/models/medicion.dto.ts` (nuevo: `MedicionBiometricaDTO` alineado a `docs/openapi.yaml`)
  - `frontend/src/app/core/services/medicion.service.ts` (nuevo: `MedicionService` con `registrar()` y `listarPorPaciente()`)
  - `frontend/src/app/pages/mediciones-list/` (nuevo: tabla de mediciones, botón nueva medición, volver al paciente)
  - `frontend/src/app/pages/medicion-nuevo/` (nuevo: formulario con validación min=0 en pesoKg/grasaPct, respuesta 201/400)
  - `frontend/src/app/app.routes.ts` (rutas `/pacientes/:id/mediciones` y `/pacientes/:id/mediciones/nueva`)
  - `frontend/src/app/pages/pacientes-detalle/` (botón "Mediciones biométricas" que navega al listado)
  - `todo`, `TODO.md`, `odd/tasks/frontend-angular.md` (planificación sincronizada: T5 marcado como hecho, entrada L6)
- `tsc --noEmit -p tsconfig.app.json`: exit 0 (TypeScript compila sin errores).

## Dificultad 1
**Problema:** Desde el detalle de un paciente no había acceso a sus mediciones biométricas.  
**Solución:** Agregar botón "Mediciones biométricas" en `PacientesDetalleComponent` que navega a `/pacientes/:id/mediciones`, y crear las rutas + componentes de listado y alta de mediciones.

**Por qué:** Mantener la navegación coherente con el flujo de negocio (paciente → mediciones) y reutilizar el patrón ya probado de T3.

## Por qué se construyó así
- **DTO primero:** `MedicionBiometricaDTO` se definió antes que el servicio y los componentes, alineado al schema OpenAPI.
- **Servicio reutilizando ApiService:** `MedicionService` usa `API_BASE_URL` de `api.config.ts` sin duplicar la lógica de URL base.
- **Validación nativa del navegador:** `min="0"` y `required` en los inputs numéricos para cumplir la regla del backend (`pesoKg >= 0`, `grasaPct >= 0`) sin añadir librerías de validación extra.
- **Campos opcionales:** `masaMuscularKg` y `cinturaCm` son opcionales en el DTO y en el formulario; el backend los acepta nulos.
- **Componentes standalone de Angular 18:** Cada página es standalone con su propio CSS/HTML, siguiendo la convención de T1/T2/T3.
- **Commit como slice de trabajo:** Tests y docs van junto al comportamiento (regla del proyecto). Los tests de endpoint frontend se dejan para T8.

## Riesgo
Ninguno nuevo: páginas de consulta y creación aisladas sobre mediciones de un paciente existente; no se tocó `opencode.json` ni `spring-petclinic/` (sin permiso).

## Día 16 — 2026-11-10: Cierre completo del Frontend Angular 18 (T4, T6, T7, T8, T9)

## Logro
- Completadas todas las tareas restantes del frontend Angular 18 en la rama `feat/recordatorios` en unidades de trabajo atómicas:
  - **T4 (`45d228b`) — Soft-delete de paciente (S4):** `PacienteService.eliminar(id): Observable<void>` contra `DELETE /api/v1/pacientes/{id}`, botón "Eliminar" con confirmación y manejo diferenciado de `204 No Content` vs `404 Not Found` tanto en `PacientesListComponent` como en `PacientesDetalleComponent`.
  - **T6 (`7bd6270`) — Revisiones mensuales y descarga PDF (S7, S8, S9):** `RevisionDTO`, `RevisionService` (`listar`, `obtenerPorId`, `crear`, `descargarPdf` con `responseType: 'blob'`), páginas `RevisionesListComponent`, `RevisionesNuevoComponent` (con selección de mediciones del paciente y cálculo automático de `deltaPeso`, `deltaGrasa` y `deltaMusculo`) y `RevisionesDetalleComponent` (detalle y descarga de `application/pdf`).
  - **T7 (`674b0d6`) — Recordatorios pendientes y feedback (S10, S11):** `RecordatorioDTO`, `RecordatorioFeedbackDTO`, `RecordatorioService` (`listarPendientes`, `actualizarFeedback`) y `RecordatoriosListComponent` con edición en línea por fila.
  - **T8 y T9 (`ce8a3ce`) — Suite de tests unitarios (S1–S11) y cierre documental:** Specs con `provideHttpClientTesting()` y `HttpTestingController` para `PacienteService`, `MedicionService`, `RevisionService`, `RecordatorioService` y `AppComponent`, además del script `npm run dev` en `frontend/package.json`.
- Verificación ejecutada:
  - `cd frontend && node_modules/.bin/tsc --noEmit -p tsconfig.app.json` → `exit 0`
  - `cd frontend && node_modules/.bin/tsc --noEmit -p tsconfig.spec.json` → `exit 0`
  - `cd frontend && npx ng test --watch=false --browsers=ChromeHeadless` → **14 of 14 SUCCESS**

## Dificultades encontradas y soluciones

### Dificultad 1 — Proyecto duplicado y ambiguo en Engram al iniciar la sesión
**Problema:** `mem_current_project` detectó dos entradas para el mismo directorio (`nutricional-coach` por `git_remote` con 19 observaciones y `nutritional coach` por nombre de carpeta con 2 observaciones antiguas), bloqueando la lectura inicial de memoria.  
**Solución:** Inspeccionar ambas entradas, confirmar que `nutricional-coach` contenía todo el progreso real hasta Día 15 y eliminar definitivamente el duplicado obsoleto con `engram delete project "nutritional coach" --hard`.  
**Por qué:** Evita dividir el historial de sesiones entre dos claves distintas y deja `nutricional-coach` como proyecto canónico único.

### Dificultad 2 — `catchError(() => of(false))` ocultaba errores 404/500 en `PacienteService.eliminar()` (T4)
**Problema:** El borrador previo en `PacienteService.eliminar()` capturaba cualquier error HTTP y emitía `false` por el canal `next`. En `PacientesListComponent`, el callback `next` filtraba al paciente de la tabla sin comprobar el booleano y el bloque `error` nunca se ejecutaba, borrando visualmente al paciente aunque el backend respondiera `404` o `500`.  
**Solución:** Cambiar la firma a `eliminar(id: string): Observable<void>` delegando directamente en `this.http.delete<void>(\`${this.base}/${id}\`)` y capturar `HttpErrorResponse` en el bloque `error` de los componentes (`err.status === 404 ? 'Paciente no encontrado.' : 'No se pudo eliminar el paciente.'`).  
**Por qué:** Preserva la semántica estándar de `HttpClient` en Angular: `next` solo se ejecuta cuando el servidor confirma la baja con `204 No Content`.

### Dificultad 3 — Descarga binaria de `application/pdf` y gestión de memoria en el navegador (T6)
**Problema:** Por defecto `HttpClient.get()` intenta parsear la respuesta como JSON, lo que falla al recibir los bytes de un PDF generado con OpenPDF; además, crear URLs temporales con `URL.createObjectURL` sin revocarlas produce fugas de memoria en la sesión del navegador.  
**Solución:** Configurar `{ responseType: 'blob' }` en `RevisionService.descargarPdf(id)`, generar una URL efímera con `window.URL.createObjectURL(blob)`, disparar la descarga mediante un elemento `<a download="revision-${id}.pdf">` y liberar inmediatamente el recurso con `window.URL.revokeObjectURL(url)`.  
**Por qué:** Garantiza que el binario PDF llegue íntegro al disco del usuario y mantiene limpio el heap del navegador.

### Dificultad 4 — Edición concurrente por fila en la tabla de recordatorios (T7)
**Problema:** Si el `[(ngModel)]` del input de feedback mutara directamente `rec.feedbackPaciente` en la tabla, un fallo `404` o de red en `PATCH /api/v1/recordatorios/{id}` dejaría la UI mostrando un dato no persistido.  
**Solución:** Mantener un diccionario auxiliar `feedbackPorId: Record<string, string>` indexado por `id` del recordatorio y reemplazar el registro en `this.recordatorios` únicamente cuando el `PATCH` devuelve `200 OK` con el `RecordatorioDTO` actualizado.  
**Por qué:** Desacopla el estado del formulario en edición del estado confirmado por el backend.

### Dificultad 5 — Directivas de navegación en componente standalone `AppComponent` y test inicial roto (T8)
**Problema:** `app.component.html` utilizaba `routerLink` y `routerLinkActive`, pero `AppComponent` solo importaba `RouterOutlet`; además, el test autogenerado `app.component.spec.ts` buscaba un `<h1>Hello, frontend</h1>` que ya había sido reemplazado por la barra `<nav>`.  
**Solución:** Añadir `RouterLink` y `RouterLinkActive` a `imports` en `AppComponent` y actualizar `app.component.spec.ts` proveyendo `provideRouter([])` y verificando los enlaces de navegación (`Pacientes`, `Revisiones`, `Recordatorios`).  
**Por qué:** En Angular 18 standalone, cada directiva usada en el template debe declararse explícitamente en el componente para que el router intercepte la navegación sin recargar la página.

### Dificultad 6 — Hallazgo en el mapeo de `POST /pacientes/{id}/revisiones` en el backend (para abordar mañana)
**Problema:** Al revisar `RevisionController.java` (sin modificarlo por la regla de no tocar `src/` sin permiso explícito), se detectó que la clase tiene `@RequestMapping("/api/v1/revisiones")` y el método `crearRevision` tiene `@PostMapping("/pacientes/{id}/revisiones")`, lo que en Spring Boot resuelve a `/api/v1/revisiones/pacientes/{id}/revisiones` en vez de `/api/v1/pacientes/{id}/revisiones` como define `docs/openapi.yaml`.  
**Solución:** El frontend (`RevisionService.crear`) se construyó respetando estrictamente `docs/openapi.yaml` (`POST /api/v1/pacientes/{id}/revisiones`), y se dejó anotado en los pendientes de mañana ajustar el path en `RevisionController.java` durante la prueba de integración E2E.  
**Por qué:** Respeta simultáneamente la regla "Spec primero (`docs/openapi.yaml`)" y el límite de no modificar `src/` sin autorización.

## Por qué se construyó así (Decisiones de Arquitectura Frontend)
1. **Alineación estricta Spec-First (`docs/openapi.yaml`):** Todos los modelos (`PacienteDTO`, `MedicionBiometricaDTO`, `RevisionDTO`, `RecordatorioDTO`, `RecordatorioFeedbackDTO`) reflejan exactamente los nombres y tipos de los schemas OpenAPI antes de escribir la lógica de servicios o vistas.
2. **Capa de servicios desacoplada por dominio:** Cada agregado del dominio (`PacienteService`, `MedicionService`, `RevisionService`, `RecordatorioService`) encapsula las rutas y verbos HTTP a partir de `API_BASE_URL`, evitando URLs hardcodeadas en los componentes visuales.
3. **Componentes Standalone de Angular 18 sin dependencias pesadas:** Se utilizaron componentes standalone (`CommonModule`, `FormsModule`) y validación HTML5 nativa (`required`, `min="0"`, `step="0.1"`), logrando un bundle ligero, compilación rápida y cero librerías externas innecesarias.
4. **Cálculo reactivo de deltas con opción de ajuste manual (T6):** En `RevisionesNuevoComponent`, al seleccionar la medición anterior y la actual de un paciente se calculan automáticamente `deltaPeso`, `deltaGrasa` y `deltaMusculo` (redondeados a 2 decimales), pero los campos siguen siendo editables por si el nutricionista registra una revisión manual.
5. **Testing determinista con `provideHttpClientTesting()` (T8):** Los tests unitarios de servicios verifican método HTTP, URL exacta, payload enviado, tipo de respuesta (`json` o `blob`) y manejo de códigos `200`, `201`, `204` y `404` mediante `HttpTestingController`, ejecutándose en `0.04s` en `ChromeHeadless` sin depender de que el backend esté levantado.

## Riesgo
Ninguno nuevo en el frontend: las tareas T1–T9 están cerradas y verificadas con `tsc --noEmit` y 14/14 tests en Karma/ChromeHeadless. Queda documentado el ajuste de ruta en `RevisionController.java` para la prueba E2E de mañana.

## Día 14 — 2026-11-06: Frontend T3 páginas de pacientes (alta, listar, detalle)

## Logro
- Implementado T3 del frontend Angular 18 (commit `8bd4c12` en rama `feat/recordatorios`): páginas de pacientes para alta (S1), listado (S2) y detalle (S3), conectadas a los endpoints del backend en `http://localhost:8080/api/v1`.
- Archivos nuevos/modificados:
  - `frontend/src/app/core/models/paciente.dto.ts` (nuevo: `PacienteDTO` con `id?`, `nombre`, `apellidos`, `email`, `telefono?`, `objetivo?`, `fechaAlta?`, alineado a `docs/openapi.yaml`)
  - `frontend/src/app/core/services/paciente.service.ts` (nuevo: `PacienteService` con `listar(page, size)`, `obtenerPorId(id)`, `crear(paciente)` usando `ApiService`)
  - `frontend/src/app/pages/pacientes-list/` (nuevo: tabla de pacientes activos, paginación page/size, navegación al detalle)
  - `frontend/src/app/pages/pacientes-nuevo/` (nuevo: formulario de alta con nombre/email obligatorios, respuesta 201 Created + Location o 400)
  - `frontend/src/app/pages/pacientes-detalle/` (nuevo: detalle de paciente por ID, 200 con PacienteDTO o 404 si no existe)
  - `frontend/src/app/app.routes.ts` (rutas `/pacientes`, `/pacientes/nuevo`, `/pacientes/:id`)
  - `frontend/src/app/app.component.html` (navegación entre páginas de pacientes)
  - `frontend/proxy.conf.json` (nuevo: proxy de `/api/v1` a `http://localhost:8080` para evitar CORS entre `:4200` y `:8080`)
  - `frontend/package.json` (script `start` actualizado con `--proxy-config proxy.conf.json`)
  - `todo`, `TODO.md`, `odd/tasks/frontend-angular.md` (planificación sincronizada: T3 marcado como hecho, entrada L5)
- `tsc --noEmit -p tsconfig.app.json`: exit 0 (TypeScript compila sin errores).
- Commit: `8bd4c12 feat(frontend): páginas de pacientes (alta, listar, detalle)` — 18 archivos, +413/−340.

## Dificultad 1
**Problema:** `node_modules/.bin/tsc` no se encontraba invocando `tsc` directamente desde bash sin `cd frontend`.  
**Solución:** Ejecutar `cd frontend && node_modules/.bin/tsc --noEmit -p tsconfig.app.json`.  

**Por qué:** El ejecutable de TypeScript vive dentro de `frontend/node_modules/.bin`; debe invocarse desde esa carpeta o con ruta absoluta.

## Dificultad 2
**Problema:** CORS entre el frontend (`http://localhost:4200`) y el backend (`http://localhost:8080`).  
**Solución:** Agregar `proxy.conf.json` que redirige `/api/v1` a `http://localhost:8080` y actualizar el script `start` de Angular con `--proxy-config proxy.conf.json`.  

**Por qué:** El backend debe aceptar peticiones desde `http://localhost:4200` (regla del proyecto: CORS para desarrollo local). El proxy de Angular es la forma más simple y estándar de evitar CORS en desarrollo sin tocar la configuración del backend.

## Por qué se construyó así
- **DTO primero:** `PacienteDTO` se definió antes que el servicio y los componentes, siguiendo la regla del proyecto "Spec primero: cualquier endpoint nuevo va antes en `docs/openapi.yaml`, luego en Java" (en el frontend, el DTO refleja el schema OpenAPI antes de consumirlo).
- **Servicio centralizado:** `PacienteService` usa `ApiService` (ya existente de T2) para no duplicar la lógica de resolución de URL base (`http://localhost:8080/api/v1`) en cada componente.
- **Componentes standalone de Angular 18:** Cada página es un componente standalone con su propio CSS/HTML, siguiendo la convención de Angular 18 y la estructura ya generada en T1/T2.
- **Formulario con FormsModule y validación nativa:** `required` en inputs de nombre y email para cumplir S1 (400 si falta nombre/email), sin añadir librerías de validación extra.
- **Proxy en lugar de CORS en backend:** Se eligió configurar el proxy de Angular para desarrollo local, manteniendo el backend sin cambios y respetando la regla de no tocar código sin permiso.
- **Commit como slice de trabajo:** Los tests y docs van junto al comportamiento (regla del proyecto: todo cambio debe tener test que pase; en el frontend, los tests de endpoint se dejan para T8).

## Riesgo
Ninguno nuevo: páginas de consulta y creación aisladas sobre pacientes; no se tocó `opencode.json` ni `spring-petclinic/` (sin permiso). El proxy solo afecta al entorno de desarrollo local del frontend.