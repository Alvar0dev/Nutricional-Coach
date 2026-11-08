# Feature: Frontend Angular 18 conectado a los endpoints de la API

Referencia: `docs/openapi.yaml` (especificación OpenAPI 3.0.3, `servers: http://localhost:8080`)
Especificación: `docs/constitution.md` §3 (alineación spec-código)

## Specs (S#)

- **S1**: El frontend debe consumir `POST /api/v1/pacientes` (alta de paciente, 201 Created + Location, 400 sin nombre/email). Cuerpo: `PacienteDTO` (`nombre`, `apellidos`, `email`, `telefono`, `objetivo`).
- **S2**: El frontend debe consumir `GET /api/v1/pacientes` (listar solo activos, paginación `page`/`size`, 200 con lista `PacienteDTO`).
- **S3**: El frontend debe consumir `GET /api/v1/pacientes/{id}` (obtener paciente por ID, 200 con `PacienteDTO`, 404 si no existe).
- **S4**: El frontend debe consumir `DELETE /api/v1/pacientes/{id}` (soft-delete, 204/404).
- **S5**: El frontend debe consumir `POST /api/v1/pacientes/{id}/mediciones` (registrar medición, 201 Created + Location). Cuerpo: `MedicionBiometricaDTO` (`fecha`, `pesoKg`, `grasaPct`, `masaMuscularKg`, `cinturaCm`).
- **S6**: El frontend debe consumir `GET /api/v1/pacientes/{id}/mediciones` (listar mediciones de un paciente, 200 con lista).
- **S7**: El frontend debe consumir `GET /api/v1/revisiones` y `GET /api/v1/revisiones/{id}` (revisiones mensuales, 200/404).
- **S8**: El frontend debe consumir `POST /api/v1/pacientes/{id}/revisiones` (crear revisión con deltas, 201 Created + Location). Cuerpo: `RevisionDTO`.
- **S9**: El frontend debe consumir `GET /api/v1/revisiones/{id}/pdf` (descargar PDF de plan de dieta/entreno, `application/pdf`).
- **S10**: El frontend debe consumir `GET /api/v1/recordatorios` (listar recordatorios pendientes, 200 con lista `RecordatorioDTO`).
- **S11**: El frontend debe consumir `PATCH /api/v1/recordatorios/{id}` (actualizar feedback del paciente, 200 con `RecordatorioDTO`, 404 si no existe). Cuerpo: `RecordatorioFeedbackDTO` (`feedbackPaciente`).
- **S12**: El frontend debe ejecutarse con `npm run dev` (Angular 18, dev server) y conectarse a `http://localhost:8080`.

## Tareas

| ID | Spec | Ruta | Estado |
|----|------|------|--------|
| T1 | S12 | Generar proyecto Angular 18 (`npm run dev`) | Hecho |
| T2 | S12 | Configurar servicio HTTP base (`http://localhost:8080/api/v1`) | Hecho |
| T3 | S1, S2, S3 | Páginas/componentes de pacientes (alta, listar, detalle) | Hecho |
| T4 | S4 | Acción de soft-delete de paciente | Pendiente |
| T5 | S5, S6 | Formulario y listado de mediciones biométricas por paciente | Hecho |
| T6 | S7, S8, S9 | Páginas de revisiones mensuales (listar, crear, descargar PDF) | Pendiente |
| T7 | S10, S11 | Página de recordatorios pendientes + actualizar feedback | Pendiente |
| T8 | S1-S11 | Tests que pinchen cada endpoint (200/201/404) | Pendiente |
| T9 | S1-S11 | Documentar dificultades y comandos en `docs/CHANGELOG.md` | Pendiente |

## Log

- **L1** (2026-11-05): Usuario solicita hacer el frontend Angular 18 (tarea 2 del MVP) y el PR de `feat/recordatorios` a `main` (tarea 4), documentando dificultades, commiteando y pusheando.
- **L2** (2026-11-05): `feat/recordatorios` ya pusheado a `origin/feat/recordatorios` (commits 171b197, e7867a0, 61ac33a, b958430 + 8feebdf docs). El PR a `main` no se pudo crear con `gh` porque la CLI no está autenticada (`gh auth login` requerido).
- **L3** (2026-11-05): No existe proyecto Angular en el repo (no hay `package.json` ni carpeta frontend). Entorno: Node v24.21.0, npm 12.2.1 disponibles; `@angular/cli` no instalado globalmente (usar `npx`).
- **L4** (2026-11-06): T1 y T2 completados. Proyecto Angular 18 generado en `frontend/` (`npx --yes @angular/cli@18 new frontend --routing --style=css --skip-git --package-manager=npm`). `npm install` ejecutado; dependencias listas. `src/app/core/api.config.ts` con `API_BASE_URL = 'http://localhost:8080/api/v1'`; `src/app/core/api.service.ts` con `ApiService` inyectable (`get`, `post`, `patch`, `delete` anteponiendo la URL base); `src/app/app.config.ts` con `provideHttpClient()`. `tsc --noEmit -p tsconfig.app.json` exit 0 (sin errores).
- **L5** (2026-11-06): T3 completado. Páginas de pacientes implementadas: `PacientesListComponent` (S2, GET /pacientes, tabla de activos con paginación page/size), `PacientesNuevoComponent` (S1, POST /pacientes, 201 Created + Location, 400 sin nombre/email), `PacientesDetalleComponent` (S3, GET /pacientes/{id}, 200 con PacienteDTO, 404 si no existe). Modelo `PacienteDTO` en `core/models/paciente.dto.ts` y servicio `PacienteService` en `core/services/paciente.service.ts` usando `ApiService`. Rutas en `app.routes.ts`, navegación en `app.component.html`, proxy `proxy.conf.json` para evitar CORS :4200->:8080, script `start` actualizado con `--proxy-config proxy.conf.json`. `tsc --noEmit -p tsconfig.app.json` exit 0.

- **L6** (2026-11-07): T5 completado. Mediciones biométricas por paciente implementadas siguiendo el patrón de T3:
  - `core/models/medicion.dto.ts` (nuevo: `MedicionBiometricaDTO` con `id?`, `pacienteId?`, `fecha`, `pesoKg`, `grasaPct`, `masaMuscularKg?`, `cinturaCm?`, alineado a `docs/openapi.yaml`)
  - `core/services/medicion.service.ts` (nuevo: `MedicionService` con `registrar(pacienteId, medicion)` POST → 201 Created + Location y `listarPorPaciente(pacienteId)` GET → 200 con lista)
  - `pages/mediciones-list/` (nuevo: S6, tabla de mediciones del paciente, botón "Nueva medición", volver al paciente)
  - `pages/medicion-nuevo/` (nuevo: S5, formulario con validación `min="0"` en pesoKg y grasaPct, respuesta 201/400, mensaje de éxito)
  - `app.routes.ts` (rutas `/pacientes/:id/mediciones` y `/pacientes/:id/mediciones/nueva`)
  - `pages/pacientes-detalle/` (botón "Mediciones biométricas" que navega al listado de mediciones del paciente)
  - `tsc --noEmit -p tsconfig.app.json`: exit 0 (TypeScript compila sin errores).

## Dificultad 1 (T5)
**Problema:** Al navegar al detalle de un paciente no había forma de acceder a sus mediciones biométricas.  
**Solución:** Agregar un botón "Mediciones biométricas" en `PacientesDetalleComponent` que navega a `/pacientes/:id/mediciones`, y crear las rutas + componentes de listado y alta de mediciones.

**Por qué:** Mantener la navegación coerente con el flujo de negocio (paciente → mediciones) y reutilizar el patrón ya probado de T3.

## Por qué se construyó así (T5)
- **DTO primero:** `MedicionBiometricaDTO` se definió antes que el servicio y los componentes, alineado al schema OpenAPI (`fecha`, `pesoKg`, `grasaPct`, `masaMuscularKg`, `cinturaCm`).
- **Servicio reutilizando ApiService:** `MedicionService` no duplica la lógica de URL base; usa `API_BASE_URL` de `api.config.ts` igual que `PacienteService`.
- **Validación nativa del navegador:** `min="0"` y `required` en los inputs numéricos para cumplir la regla del backend (`pesoKg >= 0`, `grasaPct >= 0`) sin añadir librerías de validación extra.
- **Campos opcionales:** `masaMuscularKg` y `cinturaCm` son opcionales en el DTO y en el formulario; el backend los acepta nulos.
- **Componentes standalone de Angular 18:** Cada página es standalone con su propio CSS/HTML, siguiendo la convención de T1/T2/T3.
- **Commit como slice de trabajo:** Tests y docs van junto al comportamiento (regla del proyecto). Los tests de endpoint frontend se dejan para T8.

## Riesgo (T5)
Ninguno nuevo: páginas de consulta y creación aisladas sobre mediciones de un paciente existente; no se tocó `opencode.json` ni `spring-petclinic/` (sin permiso).

## Risk

Risk: item 5 (delivery/environment — frontend nuevo, dependencias, puerto, CORS contra `http://localhost:8080`). Mitigación: generar proyecto Angular 18 isolado, configurar proxy a backend, tests que pinchen cada endpoint contra la spec, no tocar `opencode.json` ni `spring-petclinic/` (sin permiso).
