# Plan pendiente hasta el MVP — Nutricional Coach

> Ábrelo en VS Code cuando inicies una sesión. Marca lo que vayas haciendo y avisa si cambia el alcance.

## Estado actual (2026-10-25)

- Rama activa: `feat/soft-delete-mediciones-biometricas`
- Último commit: `7513c36` — `feat(pacientes): listar pacientes activos con GET /api/v1/pacientes`
- Suite de tests: **13/13 verdes** (`mvn21.cmd test`)
- Entorno listo: `mvn21.cmd` (Java 21 + Maven 3.9.9) en `C:\Users\alvar\bin\mvn21.cmd`

## Hecho (cierra delete-paciente + medicion-biometrica POST)

- `DELETE /api/v1/pacientes/{id}` — soft-delete (activo=false + fechaBaja), 204/404
- `GET /api/v1/pacientes` — listar solo activos, paginación `page`/`size`, 200 (hoy)
- `POST /api/v1/pacientes/{id}/mediciones` — 201 Created + Location header, validación pesoKg/grasaPct >= 0

## Pendiente para el MVP (en orden sugerido)

### Sesión siguiente (corta)
- [ ] **T2 medicion-biometrica:** `GET /api/v1/pacientes/{id}/mediciones` — listar mediciones de un paciente (200, 404 si no existe). Requiere service + controller + tests.
- [ ] **T3 medicion-biometrica:** Documentar inmutabilidad en `docs/CHANGELOG.md`.

### Sesiones siguientes (hasta MVP)
- [ ] `POST /api/v1/pacientes` — alta de paciente (falta en controller; existe en openapi.yaml)
- [ ] `GET /api/v1/pacientes/{id}` — obtener paciente por ID (200/404)
- [ ] `GET /api/v1/revisiones` y `GET /api/v1/revisiones/{id}` — revisiones mensuales (en openapi.yaml, sin controller)
- [ ] `GET /api/v1/recordatorios` — alertas mensuales (en openapi.yaml, sin controller)
- [ ] Generación de PDF de plan de dieta/entrenamiento (OpenPDF)
- [ ] Frontend Angular 18 (`npm run dev`) conectado a los endpoints
- [ ] `.env` con `DB_URL`, `DB_USER`, `DB_PASSWORD`, `AWS_REGION`, credenciales S3 (para RDS/S3)
- [ ] PR de `feat/soft-delete-mediciones-biometricas` a `main` (tu decisión)

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

Última actualización: sesión del 2026-10-25. Si cambias el plan, edita este archivo y avísame.
