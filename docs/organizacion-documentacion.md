# Organización de documentación — Nutricional Coach

Este documento explica cómo está organizada la documentación del proyecto y **por qué** se organizó así, siguiendo las buenas prácticas de **Organic Driven Development (ODD)**.

## Principio rector: ODD no exige burocracia de documentos

ODD distingue por tamaño de tarea:

| Tamaño | Criterio | Documentos requeridos |
|--------|----------|----------------------|
| **Small** | Entendido, riesgo contenido, resumible desde `git diff` | **Ninguno obligatorio**. `todo` opcional. |
| **Large** | Varias sesiones, entrega separada, requisitos que no caben en el diff | **Un solo documento de feature**: `odd/tasks/<feature>.md` + espejo en Engram. |

De esto se deriva que **no todos los documentos son necesarios siempre**. Solo se crea lo que el tamaño de la tarea realmente requiere.

## Mapa actual de documentos

| Documento | Ruta exacta | Propósito | ¿Editar por sesión? |
|-----------|-------------|-----------|---------------------|
| Navegación + estado actual | `PROJECT_INDEX.md` | Mapa de navegación, contratos API, stack, puntos de entrada. | Solo cuando cambia la estructura o los contratos. |
| Comandos de desarrollo | `DEV_RUNBOOK.md` | Comandos exactos de build/test/run, entornos, gotchas. | Solo cuando cambian los comandos o entornos. |
| Contexto operativo | `AGENTS.md` | Stack, tecnologías, comandos principales, límites de edición. | Solo cuando cambia el setup o las convenciones. |
| Bitácora histórica | `README.md` | Registro día a día, stack, flujo de negocio, tecnologías. | **No editar por sesión.** Es memoria histórica. |
| Principios | `docs/constitution.md` | Principios innegociables (simplicidad, separación, spec primero, testing obligatorio, etc.). | Solo cuando cambian los principios. |
| Changelog | `docs/CHANGELOG.md` | Historia de cambios, errores y dificultades por jornada. | Solo al cerrar jornadas o hitos. |
| Especificación API | `docs/openapi.yaml` | Fuente de verdad de contratos (OpenAPI 3.0). | Solo cuando cambia la API. |
| Mapa de docs | `docs/organizacion-documentacion.md` | Qué es cada documento, porqué se organizó así, reglas ODD. | Solo cuando cambia la organización de docs. |
| Estado actual ODD | `odd/task/estado-actual-proyecto.md` | **Único archivo de definición de estado actual** del proyecto. Punto de partida para la siguiente sesión. | Solo cuando cambia el estado base del proyecto. |
| Feature tracking | `odd/tasks/<feature>.md` | `## Specs`, `## Tasks`, `## Log` para trabajos grandes (ej. `delete-paciente.md`, `medicion-biometrica.md`). | Solo durante una feature activa y grande. |
| Archivo de features | `odd/tasks/archive/` | Feature trackings ya cerrados. | Nunca. |
| Checklist sesión | `todo` (raíz, sin extensión) | Checklist ligero de la **sesión actual**. | Sí, se actualiza cada sesión. |
| Plan MVP | `PLAN_PENDIENTE_MVP.md` | Plan de trabajo hasta el MVP, tareas por hacer y hechas. | Cuando cambia el alcance del MVP. |
| Gitignore | `.gitignore` | Ignora `target/`, `node_modules/`, `dist/`, `.angular/`, `.env`, etc. | Solo cuando haya que ignorar algo nuevo. |

## Reglas ODD para documentación (cuándo crear qué)

| Tamaño de tarea | Criterio | Documentos requeridos |
|-----------------|----------|----------------------|
| **Small** | Entendido, riesgo contenido, resumible desde `git diff` | Ninguno obligatorio. `todo` opcional. |
| **Large** | Varias sesiones, entrega separada, requisitos que no caben en el diff | Un solo documento de feature: `odd/tasks/<feature>.md` + espejo en Engram. |

Regla de oro: no crear documentos por inercia. Solo lo que el tamaño de la tarea requiere.

### El "único archivo de definición de estado actual"

Es `odd/task/estado-actual-proyecto.md`. Es el punto de partida para la siguiente sesión. Se edita solo cuando cambia el estado base del proyecto.

### Qué va al archivo `todo` vs qué va al feature doc

| | `todo` (raíz) | `odd/tasks/<feature>.md` |
|---|---------------|--------------------------|
| Alcance | Sesión actual (hoy) | Feature completa (varias sesiones) |
| Contenido | Checklist ligero `[ ]` / `[x]` | `## Specs`, `## Tasks`, `## Log` |
| Ciclo de vida | Se actualiza cada sesión | Se crea antes del primer write, se cierra al terminar, se mueve a `archive/` |
| ¿Editar por sesión? | Sí | Solo durante una feature activa y grande |

### Flujo de trabajo resumido

```
Nueva sesión
    │
    ▼
Leer odd/task/estado-actual-proyecto.md  (estado base)
    │
    ▼
¿Trabajo small o large?
    │
    ├─ small ──► editar código, correr tests, commitear. Sin documentos nuevos.
    │
    └─ large ──► crear odd/tasks/<feature>.md (Specs, Tasks, Log)
                   │
                   ▹ trabajar tarea por tarea
                   │
                   ▼
                 cerrar feature, mover a odd/tasks/archive/
```

## Pasos para replicar en otro proyecto

1. Creá la estructura de carpetas base: `docs/`, `odd/task/`, `odd/tasks/archive/`.
2. Creá `docs/constitution.md` primero (principios innegociables).
3. Creá `PROJECT_INDEX.md` (navegación + contratos + estado).
4. Creá `DEV_RUNBOOK.md` (comandos de build/test/run).
5. Creá `AGENTS.md` (contexto operativo breve).
6. Creá `README.md` como bitácora histórica (no editar por sesión).
7. Creá `todo` en la raíz (checklist de sesión).
8. Creá `.gitignore` (binarios, `target/`, `node_modules/`, `.env`, etc.).
9. Creá `odd/task/estado-actual-proyecto.md` (único archivo de definición de estado actual).
10. Creá `docs/organizacion-documentacion.md` (este mapa y porqué).
11. Cuando termine una feature grande, mové `odd/tasks/<feature>.md` a `odd/tasks/archive/`.