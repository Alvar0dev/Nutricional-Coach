# TODO — Nutricional Coach (MVP)

## Hasta el MVP
- [ ] `POST /api/v1/pacientes` — alta de paciente (falta controller; está en openapi.yaml)
- [ ] `GET /api/v1/pacientes/{id}` — obtener paciente por ID (200/404)
- [ ] `GET /api/v1/revisiones` y `GET /api/v1/revisiones/{id}` — revisiones mensuales
- [ ] Generación de PDF de plan de dieta/entrenamiento (OpenPDF)
- [ ] Frontend Angular 18 (`npm run dev`) conectado a los endpoints
- [ ] `.env` con `DB_URL`, `DB_USER`, `DB_PASSWORD`, `AWS_REGION`, credenciales S3 (RDS/S3)
- [ ] PR de `feat/recordatorios` a `main` (decisión tuya)

## Hecho (en feat/recordatorios, sin pushear aún)
- [x] `PATCH /api/v1/recordatorios/{id}` — actualizar feedback del paciente (openapi.yaml + controller + service + tests)
- [x] `GET /api/v1/recordatorios` — alertas mensuales pendientes (200 con lista de RecordatorioDTO)
- [x] `DELETE /api/v1/pacientes/{id}` — soft-delete (204/404)
- [x] `GET /api/v1/pacientes` — listar solo activos, paginación `page`/`size`
- [x] `POST /api/v1/pacientes/{id}/mediciones` — 201 Created + Location header
- [x] `GET /api/v1/pacientes/{id}` — obtener paciente por ID (200/404)
- [x] `POST /api/v1/pacientes` — alta de paciente (201 Created, Location header)
- [x] `GET /api/v1/revisiones` y `GET /api/v1/revisiones/{id}` — revisiones mensuales (200/404/201)

## Comandos clave (terminal integrada de VS Code)
```
mvn21.cmd clean compile   # compilar
mvn21.cmd test            # tests (35/35 verdes)
git status --short --branch
```
