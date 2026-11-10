# Handoff — Estado tras completar Frontend Angular 18 (T1–T9) y Plan para Mañana

## Contexto (2026-11-10)
- **Repo:** `G:\Proyectos OpenCode\BBVA\Nutritional Coach`
- **Rama activa:** `feat/recordatorios` (sincronizada con `origin/feat/recordatorios`)
- **Backend:** Spring Boot 3.4 + Java 21, endpoints en `http://localhost:8080/api/v1`, suite **39/39 tests verdes** (`mvn21.cmd test`).
- **Frontend:** Angular 18 en `frontend/`, `tsc --noEmit` limpio (`tsconfig.app.json` y `tsconfig.spec.json`), **14/14 tests unitarios verdes** en `ChromeHeadless` (`npx ng test --watch=false --browsers=ChromeHeadless`), proxy `:4200 -> :8080` en `proxy.conf.json`.

## Lo completado hoy (Día 16 — 2026-11-10)
1. **Resolución de proyecto en Engram:** Eliminado el duplicado obsoleto `"nutritional coach"`, dejando `nutricional-coach` como proyecto canónico único.
2. **T4 (`45d228b`) — Soft-delete de paciente (S4):** `PacienteService.eliminar(id): Observable<void>` sin tragar errores HTTP, confirmación y manejo de 204/404 en listado y detalle.
3. **T6 (`7bd6270`) — Revisiones mensuales y descarga PDF (S7, S8, S9):** `RevisionDTO`, `RevisionService`, `RevisionesListComponent`, `RevisionesNuevoComponent` (cálculo automático de deltas entre mediciones) y `RevisionesDetalleComponent` (descarga de `application/pdf` con `Blob` y `URL.createObjectURL`).
4. **T7 (`674b0d6`) — Recordatorios pendientes y feedback (S10, S11):** `RecordatorioDTO`, `RecordatorioFeedbackDTO`, `RecordatorioService` y `RecordatoriosListComponent` con edición en línea mediante `PATCH /api/v1/recordatorios/{id}`.
5. **T8 y T9 (`ce8a3ce`) — Tests unitarios de servicios (S1–S11) y cierre documental:** 14 specs en verde cubriendo `PacienteService`, `MedicionService`, `RevisionService`, `RecordatorioService` y `AppComponent`, más registro detallado en `docs/CHANGELOG.md`.

## TODOs para mañana (en orden de ejecución)

### 1. Alinear ruta de alta de revisión en Backend y prueba E2E (`:4200` ↔ `:8080`)
- **Problema detectado hoy:** En `src/main/java/com/nutricionalcoach/controller/RevisionController.java`, la anotación de clase `@RequestMapping("/api/v1/revisiones")` sumada a `@PostMapping("/pacientes/{id}/revisiones")` hace que Spring Boot exponga `/api/v1/revisiones/pacientes/{id}/revisiones` en lugar de `/api/v1/pacientes/{id}/revisiones` (definido en `docs/openapi.yaml` y consumido por `RevisionService.crear` en el frontend).
- **Acción:** Con autorización para editar `src/`, ajustar el mapping en `RevisionController.java`, verificar `mvn21.cmd test` y levantar simultáneamente backend (`mvn21.cmd spring-boot:run`) y frontend (`cd frontend && npm run dev`) para validar el circuito de negocio de punta a punta en el navegador.

### 2. Configuración de entorno e infraestructura (`.env` / AWS RDS & S3)
- **Acción:** Crear plantilla `.env.example` y archivo local `.env` (ignorado por Git) con `DB_URL`, `DB_USER`, `DB_PASSWORD`, `AWS_REGION` y credenciales S3.

### 3. Pull Request de `feat/recordatorios` a `main`
- **Acción:** Autenticar GitHub CLI (`gh auth login`) o abrir el Pull Request directamente en GitHub desde `feat/recordatorios` hacia `main`.
