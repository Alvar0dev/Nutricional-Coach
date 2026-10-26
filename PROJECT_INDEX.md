# PROJECT_INDEX.md — Mapa de navegación arquitectónico

## Propósito
Plataforma cloud de nutrición deportiva y biometría. Flujo: alta paciente → medición biométrica → alertas → revisión mensual → PDF (dieta/entreno). Metodología ODD (Organic Driven Development).

## Stack principal
- Runtime: Java 21 (proyecto principal) / Java 17 (spring-petclinic subproyecto)
- Backend: Spring Boot 3.4 (esperado), Hibernate JPA, Maven/Gradle
- API: OpenAPI 3.0 (`docs/openapi.yaml` — fuente de verdad de contratos)
- Frontend: Angular 18, Thymeleaf, OpenPDF
- Cloud: AWS Free Tier (EC2, RDS, S3) — planeado
- Tests: JUnit 5 + Mockito (ejemplo: `PacienteControllerTest.java`)

## Estructura de carpetas (responsabilidad)

- `docs/` — Documentación de arquitectura y contratos (`openapi.yaml`, `CHANGELOG.md`, `constitution.md`). **No duplicar reglas de negocio aquí; referenciar por archivo.**
- `odd/` — Tracking de tareas por feature (`odd/tasks/delete-paciente.md`). Cada feature apunta a `S#` en `docs/openapi.yaml`.
- `src/` — Código fuente del proyecto Nutricional-Coach (nuevo). Estructura: `controller/`, `service/`, `test/`. Aún no completo; solo `PacienteController.java` implementado como avance día 5.
- `spring-petclinic/` — Subproyecto independiente (gitlink a `spring-projects/spring-petclinic`). No modificar sin permiso; usa Gradle + Java 17 (`build.gradle`).
- `AGENTS.md` — Contexto operativo del proyecto (stack, comandos, límites de edición, convención ODD).
- `.atl/skill-registry.md` — Registro de habilidades para subagentes.
- `.vscode/` — Configuración de IDE (no tocar sin confirmar).

## Puntos de entrada (entry points)

- **API REST**: `docs/openapi.yaml` → `PacientesController` (`/api/v1/pacientes`). Endpoint implementado: `DELETE /api/v1/pacientes/{id}` (`PacienteController.java`). Otros endpoints definidos en spec pero sin controller aún.
- **Controlador existente**: `src/main/java/com/nutricionalcoach/controller/PacienteController.java`
- **Servicio**: `src/main/java/com/nutricionalcoach/service/PacienteService.java` (interfaz; implementación pendiente)
- **Test**: `src/test/java/com/nutricionalcoach/controller/PacienteControllerTest.java`
- **Subproyecto**: `spring-petclinic/src/main/java/.../VetController.java` — referencia de Spring Boot, no del flujo de negocio del coach.

## Contratos clave (rutas/firmas representativas)

- `DELETE /api/v1/pacientes/{id}` → `PacienteController.deletePaciente(String id)` → `PacienteService.softDeletePaciente(String id)` → respuesta `204` / `404`
- `POST /api/v1/pacientes/{id}/mediciones` → definido en `openapi.yaml`; controller pendiente
- `GET /api/v1/revisiones/{id}/pdf` → definido; controller pendiente
- `POST /api/v1/pacientes/{id}/revisiones` → definido; controller pendiente

## Flujo de datos crítico (request → persistencia)

```
Client (Angular / curl)
  → REST endpoint (PacientesController)
  → Service interface (PacienteService)
  → [Repository / JPA — pendiente implementar]
  → DB (RDS — planeado; hoy sin conexión activa en entorno local)
  → Response DTO (PacienteDTO / ErrorResponse)
```

## Reglas de negocio críticas (referenciadas por archivo, no duplicadas)

- **Soft-delete obligatorio** (item 1 Constitución): ver `docs/constitution.md` §6, `PacienteController.java` línea 26, `odd/tasks/delete-paciente.md` S3.
- **Alineación spec-código**: ver `docs/constitution.md` §3, `AGENTS.md` apartado límites.
- **Separación lógica-interfaz**: ver `docs/constitution.md` §2, `AGENTS.md` arquitectura.
- **Testing obligatorio (80% mínimo)**: ver `docs/constitution.md` §4, `odd/tasks/delete-paciente.md` S4, `PacienteControllerTest.java`.

## Estado actual (2026-10-22)
- `main`: `484fcaf` (día 5 — endpoint DELETE + soft-delete + feature doc + test)
- `docs/openapi.yaml`: completo (endpoints + schemas + ErrorResponse)
- `docs/CHANGELOG.md`: actualizado con errores/dificultades de días 1-4
- `AGENTS.md`: creado y actualizado
- `src/`: solo controlador/service/test del endpoint DELETE; resto pendiente
- `spring-petclinic/`: sin cambios; subproyecto independiente
- **No hay `pom.xml` ni `build.gradle` en raíz** para el proyecto principal; build pendiente.
