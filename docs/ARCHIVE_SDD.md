# ARCHIVE — Referencias SDD superadas

Estado: 2026-10-22. Limpieza de referencias obsoletas durante transición a Gentle Shell / ODD.

## Superado / Archivado
- Especificación intermedia de `openapi.yaml` (versión parcial con solo `/pacientes` y `/mediciones`) → reemplazada por la versión completa con todos los endpoints (`delete`, `recordatorios`, `revisiones`, `ErrorResponse`) en `docs/openapi.yaml`.
- Plan intermedio de arquitectura manual (`ARCHITECTURE_SDD.md`) → conservado como referencia de diseño inicial; reglas de negocio ahora referenciadas por archivo (`constitution.md`, `AGENTS.md`, `odd/tasks/`).
- Plantilla de feature doc `odd/tasks/delete-paciente.md` → activa tras completar T1/T2; no es obsoleta, se mantiene como registro de evidencia.

## Regla de limpieza aplicada
- No duplicar reglas de negocio en texto libre. Cada regla crítica (soft-delete, testing obligatorio, separación lógica-interfaz) está referenciada por archivo (`constitution.md`) y por código (comentarios en `PacienteController.java`, `PacienteService.java`).
- El `CHANGELOG.md` es el único registro de errores/dificultades inventados; no duplicar en archivos de diseño.
