# Plan pendiente hasta el MVP — Nutricional Coach

> Ábrelo en VS Code cuando inicies una sesión. Marca lo que vayas haciendo y avisa si cambia el alcance.

## Estado actual (2026-11-02)

- Rama activa: `feat/recordatorios` (desde `main`)
- Último commit en main: `689d278` — `feat(revisiones): GET /api/v1/revisiones y GET /api/v1/revisiones/{id} con deltas mensuales`
- Último commit en feat/recordatorios: `171b197` — `feat(recordatorios): GET /api/v1/recordatorios listar pendientes`
- Suite de tests: **33/33 verdes** (`mvn21.cmd test`)
- Entorno listo: `mvn21.cmd` (Java 21 + Maven 3.9.9) en `C:\Users\alvar\bin\mvn21.cmd`

## Hecho (cierra delete-paciente + medicion-biometrica POST + revisiones + recordatorios)

- `DELETE /api/v1/pacientes/{id}` — soft-delete (activo=false + fechaBaja), 204/404
- `GET /api/v1/pacientes` — listar solo activos, paginación `page`/`size`, 200
- `POST /api/v1/pacientes/{id}/mediciones` — 201 Created + Location header, validación pesoKg/grasaPct >= 0
- `GET /api/v1/pacientes/{id}` — obtener paciente por ID (200 con PacienteDTO, 404 si no existe)
- `POST /api/v1/pacientes` — alta de paciente (201 Created, Location header, 400 sin nombre/email)
- `GET /api/v1/revisiones` y `GET /api/v1/revisiones/{id}` — revisiones mensuales (200 con lista, 200 por ID, 404 si no existe)
- `POST /api/v1/pacientes/{id}/revisiones` — crear revisión mensual con deltas (201 Created + Location header)
- `GET /api/v1/recordatorios` — listar recordatorios pendientes (200 con lista de RecordatorioDTO, vacía si no hay ninguno)

## Pendiente para el MVP (en orden sugerido)

### Sesión siguiente (corta)
- [ ] **PATCH /api/v1/recordatorios/{id}** — actualizar feedback del paciente (en openapi.yaml, sin controller). Requiere `RecordatorioFeedbackDTO`, service + controller + tests.

### Sesiones siguientes (hasta MVP)
- [ ] `POST /api/v1/pacientes` — alta de paciente (falta en controller; existe en openapi.yaml)
- [ ] `GET /api/v1/pacientes/{id}` — obtener paciente por ID (200/404)
- [ ] Generación de PDF de plan de dieta/entrenamiento (OpenPDF) → `GET /api/v1/revisiones/{id}/pdf`
- [ ] Frontend Angular 18 (`npm run dev`) conectado a los endpoints
- [ ] `.env` con `DB_URL`, `DB_USER`, `DB_PASSWORD`, `AWS_REGION`, credenciales S3 (para RDS/S3)
- [ ] PR de `feat/recordatorios` a `main` (tu decisión)

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

Última actualización: sesión del 2026-11-02. Si cambias el plan, edita este archivo y avísame.
