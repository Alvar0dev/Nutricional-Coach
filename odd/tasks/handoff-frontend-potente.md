# Handoff Frontend Angular 18 — Para modelo potente

## Contexto
Repo: `G:\Proyectos OpenCode\BBVA\Nutritional Coach`
Rama: `feat/recordatorios` (desde `main`)
Backend: Spring Boot 3.4 + Java 21, endpoints en `http://localhost:8080/api/v1`, suite 39/39 verdes.
Frontend: Angular 18 en `frontend/`, `tsc --noEmit -p tsconfig.app.json` exit 0, proxy `:4200 -> :8080` en `proxy.conf.json`.

## Estado de T5 (hecho, verificado — commiteado en esta sesión)
- `core/models/medicion.dto.ts` — `MedicionBiometricaDTO` (fecha, pesoKg, grasaPct, masaMuscularKg?, cinturaCm?)
- `core/services/medicion.service.ts` — `registrar(pacienteId, body)` POST, `listarPorPaciente(pacienteId)` GET
- `pages/mediciones-list/` — S6: listar mediciones de un paciente, tabla, botón nueva medición, volver
- `pages/medicion-nuevo/` — S5: formulario POST medición, validación min=0 en pesoKg/grasaPct, 201/400
- `app.routes.ts` — rutas `/pacientes/:id/mediciones` y `/pacientes/:id/mediciones/nueva`
- `pages/pacientes-detalle/` — botón "Mediciones biométricas" que navega a `/pacientes/:id/mediciones`
- `tsc --noEmit` exit 0 (sin errores)

## Tareas pendientes (para modelo potente)

### T4 — Soft-delete de paciente (S4) — MUTACIÓN DESTRUCTIVA
**¿Por qué modelo potente?** Es una mutación (aunque sea soft-delete: `activo=false`). Requiere confirmación de eliminación, manejo de 204 vs 404, y actualizar la lista en la UI sin recargar.

**Endpoints backend:** `DELETE /api/v1/pacientes/{id}` → 204 / 404
**Qué implementar:**
1. Agregar `eliminar(id: string): Observable<void>` a `PacienteService` (llamar `this.http.delete(...)`).
2. Componente `pacientes-delete` (o modal en el listado/detalle) con confirmación "¿Estás seguro?".
3. Botón "Eliminar" en `pacientes-list` y/o `pacientes-detalle`.
4. Tras 204: remover el paciente de la lista local o navegar al listado con mensaje de éxito.
5. Manejo de 404: mensaje "Paciente no encontrado".
6. No tocar código compartido que no sea el servicio de pacientes.

**Archivos de referencia (patrón T3):**
- `frontend/src/app/core/services/paciente.service.ts`
- `frontend/src/app/pages/pacientes-list/pacientes-list.component.ts` + .html
- `frontend/src/app/app.routes.ts`

### T6 — Revisiones mensuales (S7, S8, S9) — MÁS DELICADO
**¿Por qué modelo potente?** Tres sub-tareas complejas:
- **S7** `GET /api/v1/revisiones` y `GET /api/v1/revisiones/{id}` — listado + detalle (lectura, sencillo).
- **S8** `POST /api/v1/pacientes/{id}/revisiones` — crear revisión con deltas calculados (mutación, mostrar deltas de peso/grasa/músculo/cintura entre mediciones).
- **S9** `GET /api/v1/revisiones/{id}/pdf` — descargar `application/pdf` desde el frontend: manejar `responseType: 'blob'`, crear `URL.createObjectURL`, disparar `<a download>`, gestionar CORS y `Content-Type`. Fácil de romper silenciosamente (archivo corrupto, CORS bloqueado, download que no se dispara).

**Endpoints backend:**
- `GET /api/v1/revisiones` → 200 lista `RevisionDTO`
- `GET /api/v1/revisiones/{id}` → 200 `RevisionDTO` / 404
- `POST /api/v1/pacientes/{id}/revisiones` → 201 Created + Location, cuerpo `RevisionDTO`
- `GET /api/v1/revisiones/{id}/pdf` → 200 `application/pdf` / 404

**Qué implementar:**
1. `core/models/revision.dto.ts` — `RevisionDTO` (id, pacienteId, fecha, deltas, etc.)
2. `core/services/revision.service.ts` — listar, obtener por id, crear, descargar PDF (con `responseType: 'blob'`).
3. `pages/revisiones-list/` — S7: tabla de revisiones.
4. `pages/revisiones-nuevo/` — S8: formulario crear revisión, mostrar deltas calculados.
5. `pages/revisiones-detalle/` — S7: detalle de una revisión + botón "Descargar PDF".
6. Lógica de descarga PDF: `HttpClient.get(..., { observe: 'response', responseType: 'blob' })`, `URL.createObjectURL(blob)`, `<a href="..." download="plan.pdf">click()`.

**Archivos de referencia:** `paciente.service.ts`, `medicion.service.ts`, `mediciones-list.component.ts`.

### T8 — Tests que pinchan cada endpoint (S1–S11)
**¿Por qué modelo potente?** Depende de la infraestructura de testing de Angular 18 (¿Karma/Jasmine? ¿Hay `HttpClientTestingModule`?). Armar mocks y asserts para 200/201/404 de todos los endpoints es tedioso y fácil de dejar con cobertura falsa.

**Qué verificar primero:**
- ¿Existe `src/test.ts` o `karma.conf.js` en `frontend/`?
- ¿Está `HttpClientTestingModule` importado en algún spec?
- ¿Hay specs existentes en `frontend/src/app/`?

**Qué implementar:**
- Un spec por componente/service que pinche cada endpoint con mocks: 200, 201, 404.
- Al menos: `paciente.service.spec.ts`, `medicion.service.spec.ts`, `revision.service.spec.ts`.

## Reglas del proyecto (no romper)
- Soft-delete obligatorio: nunca `deleteById` físico en pacientes.
- Spec primero: cualquier endpoint nuevo va antes en `docs/openapi.yaml`, luego en Java.
- Todo cambio debe tener test que pase.
- No tocar `opencode.json` (cambio LF/CRLF ajeno) ni `spring-petclinic/` (subproyecto independiente).
- CORS: frontend :4200, backend :8080, proxy configurado en `proxy.conf.json`.

## Comandos
```
# Desde G:\Proyectos OpenCode\BBVA\Nutritional Coach/frontend
node_modules/.bin/tsc --noEmit -p tsconfig.app.json   # type-check
npm start                                               # ng serve con proxy :4200->:8080
# Backend (terminal aparte):
mvn21.cmd test                                          # 39/39 verdes
```

## Siguiente acción sugerida
Empezar por T4 (soft-delete), luego T6 (revisiones + PDF), cerrar con T8 (tests). T7 (recordatorios) y T9 (documentación) pueden ir después.
