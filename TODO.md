# TODO — Nutricional Coach (MVP)

## Sesión siguiente (corta)
- [ ] T2 medicion-biometrica: `GET /api/v1/pacientes/{id}/mediciones` (listar mediciones de un paciente)
- [ ] T3 medicion-biometrica: Documentar inmutabilidad en `docs/CHANGELOG.md`

## Hasta el MVP
- [ ] `POST /api/v1/pacientes` — alta de paciente (falta controller; está en openapi.yaml)
- [ ] `GET /api/v1/pacientes/{id}` — obtener paciente por ID (200/404)
- [ ] `GET /api/v1/revisiones` y `GET /api/v1/revisiones/{id}` — revisiones mensuales
- [ ] `GET /api/v1/recordatorios` — alertas mensuales
- [ ] Generación de PDF de plan de dieta/entrenamiento (OpenPDF)
- [ ] Frontend Angular 18 (`npm run dev`) conectado a los endpoints
- [ ] `.env` con `DB_URL`, `DB_USER`, `DB_PASSWORD`, `AWS_REGION`, credenciales S3 (RDS/S3)
- [ ] PR de `main` a `origin/main` / release (decisión tuya)

## Hecho (en main)
- [x] `DELETE /api/v1/pacientes/{id}` — soft-delete (204/404)
- [x] `GET /api/v1/pacientes` — listar solo activos, paginación `page`/`size`
- [x] `POST /api/v1/pacientes/{id}/mediciones` — 201 Created + Location header

## Comandos clave (terminal integrada de VS Code)
```
mvn21.cmd clean compile   # compilar
mvn21.cmd test            # tests (13/13 verdes)
git status --short --branch
```
