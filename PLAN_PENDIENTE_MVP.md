# Plan pendiente hasta el MVP — Nutricional Coach

> Ábrelo en VS Code cuando inicies una sesión. Marca lo que vayas haciendo y avisa si cambia el alcance.

## Estado actual (2026-11-16)

- Rama activa: `main` (sincronizada con `origin/main` tras merge de PR #2, commit `033bb28`)
- MVP: **Mergeado en `main`** (commit `462393a`, PR #1 en GitHub)
- Suite de tests Backend: **43/43 verdes** (`mvn21.cmd test`, incluye E2E de contexto H2, placeholders Cloud/H2 y `DataInitializerTest` en perfil `local`)
- Suite de tests Frontend: **14/14 verdes** (`npx ng test --watch=false --browsers=ChromeHeadless`) y `tsc --noEmit` limpio
- Entorno listo: `mvn21.cmd` (Java 21 + Maven 3.9.9) en `C:\Users\alvar\bin\mvn21.cmd` y Angular 18 en `frontend/`

## Hecho (cierra delete-paciente + medicion-biometrica POST + revisiones + recordatorios)

- `DELETE /api/v1/pacientes/{id}` — soft-delete (activo=false + fechaBaja), 204/404
- `GET /api/v1/pacientes` — listar solo activos, paginación `page`/`size`, 200
- `POST /api/v1/pacientes/{id}/mediciones` — 201 Created + Location header, validación pesoKg/grasaPct >= 0
- `GET /api/v1/pacientes/{id}` — obtener paciente por ID (200 con PacienteDTO, 404 si no existe)
- `POST /api/v1/pacientes` — alta de paciente (201 Created, Location header, 400 sin nombre/email)
- `GET /api/v1/revisiones` y `GET /api/v1/revisiones/{id}` — revisiones mensuales (200 con lista, 200 por ID, 404 si no existe)
- `POST /api/v1/pacientes/{id}/revisiones` — crear revisión mensual con deltas (201 Created + Location header)
- `GET /api/v1/recordatorios` — listar recordatorios pendientes (200 con lista de RecordatorioDTO, vacía si no hay ninguno)
- `PATCH /api/v1/recordatorios/{id}` — actualizar feedback del paciente (200 con RecordatorioDTO, 404 si no existe)

## Pendiente para el MVP (en orden sugerido)

### Sesiones siguientes (hasta MVP — Plan para mañana)
- [x] `POST /api/v1/pacientes` — alta de paciente (201 Created, 400 sin nombre/email)
- [x] `GET /api/v1/pacientes/{id}` — obtener paciente por ID (200/404)
- [x] Generación de PDF de plan de dieta/entrenamiento (OpenPDF) → `GET /api/v1/revisiones/{id}/pdf` (hecho, commiteado en feat/recordatorios)
- [x] Frontend Angular 18 (`npm run dev`) conectado a los endpoints
  - [x] T1: Generar proyecto Angular 18
  - [x] T2: Servicio HTTP base
  - [x] T3: Páginas de pacientes (alta, listar, detalle)
  - [x] T5: Mediciones biométricas por paciente (registro + listado)
  - [x] T4: Soft-delete de paciente
  - [x] T6: Revisiones mensuales (listar, crear, PDF)
  - [x] T7: Recordatorios pendientes + feedback
  - [x] T8: Tests que pinchen cada endpoint (14/14 verdes en ChromeHeadless)
  - [x] T9: Documentar en docs/CHANGELOG.md
- [x] Alinear ruta `POST /api/v1/pacientes/{id}/revisiones` en `RevisionController.java`, habilitar `@CrossOrigin(origins = "http://localhost:4200")` en los 4 controladores REST y verificar con `MockMvc` (`40/40` tests verdes)
- [x] **Release / PR #1:** PR de `feat/recordatorios` a `main` creado y mergeado en GitHub (commit `462393a`, PR #1)
- [x] **Hecho (Día 19):** Dependencia H2 en `pom.xml`, clase `@SpringBootApplication`, entidades JPA y `application.properties` para arranque en vivo (suite `41/41` verdes)
- [x] **Hecho (Día 19):** Prueba de integración E2E local levantando backend (`:8080`) y frontend (`:4200`) y test `@SpringBootTest` completo
- [x] **Hecho (Día 20):** Infra / Entorno Cloud (`.env.example` versionado y `.env` local gitignored con `DB_URL`, `DB_DRIVER`, `DB_USER`, `DB_PASSWORD`, `AWS_REGION`, `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_S3_BUCKET`; `application.properties` parametrizado con fallback a H2; test en `NutricionalCoachApplicationTest`, suite **42/42 verdes**)
- [x] **Hecho (Día 20):** Demo Seed (`DataInitializer` bajo `@Profile("local")` idempotente que precarga paciente `pac-demo-1`, 2 mediciones, revisión `rev-demo-1` con PDF y recordatorio `rec-demo-1` `PENDIENTE`, verificado en `DataInitializerTest`, suite **43/43 verdes**)
- [x] **Hecho (Día 20):** Release de rama a `main` (`feat/springboot-entrypoint-h2` → `main` vía PR #2 en GitHub, mergeado y sincronizado en `main` local, commit `033bb28`)

### Siguientes pasos (Post-MVP — Preparación Cloud AWS Free Tier y Producción)
- [ ] **Mañana 1 (PostgreSQL + perfil `prod` para AWS RDS):** Añadir `org.postgresql:postgresql` (`runtime`) en `pom.xml`, crear `application-prod.properties` (sin consola H2) y test de configuración `prod`.
- [ ] **Mañana 2 (Almacenamiento PDF en S3 / local):** Definir `PdfStorageService` con implementación local y adaptador hacia `aws.s3.bucket` para los PDFs generados en `RevisionService`.
- [ ] **Mañana 3 (Empaquetado y guía de despliegue AWS EC2):** Validar `mvn21.cmd clean package -DskipTests` y `npm run build` (Angular 18) y documentar el checklist de despliegue EC2 + RDS + S3 en `DEV_RUNBOOK.md`.

## Comandos clave (usar en la terminal integrada de VS Code)

```bash
# Compilar
mvn21.cmd clean compile

# Tests
mvn21.cmd test

# Ejecutar backend (cuando esté listo)
mvn21.cmd spring-boot:run

# Git status antes de commitear/pushear
git status --short --branch
```

## Reglas de este proyecto (no romper)

- Soft-delete obligatorio: nunca `deleteById` físico en pacientes.
- Spec primero: cualquier endpoint nuevo va antes en `docs/openapi.yaml`, luego en Java.
- Todo cambio debe tener test que pase.
- `spring-petclinic/` es independiente (Java 17, Gradle); no tocar sin permiso.

---

Última actualización: sesión del 2026-11-16 (Día 20). Si cambias el plan, edita este archivo y avísame.
