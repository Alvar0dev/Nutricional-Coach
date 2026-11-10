# Plan pendiente hasta el MVP — Nutricional Coach

> Ábrelo en VS Code cuando inicies una sesión. Marca lo que vayas haciendo y avisa si cambia el alcance.

## Estado actual (2026-11-10)

- Rama activa: `feat/recordatorios` (desde `main`)
- Suite de tests Backend: **39/39 verdes** (`mvn21.cmd test`)
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
- [ ] **Mañana 1:** Alinear ruta `POST /api/v1/pacientes/{id}/revisiones` en `RevisionController.java` y ejecutar prueba E2E local (`:4200` ↔ `:8080`)
- [ ] **Mañana 2:** `.env` con `DB_URL`, `DB_USER`, `DB_PASSWORD`, `AWS_REGION`, credenciales S3 (para RDS/S3)
- [ ] **Mañana 3:** PR de `feat/recordatorios` a `main` (`gh auth login` o desde GitHub)

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

Última actualización: sesión del 2026-11-03. Si cambias el plan, edita este archivo y avísame.
