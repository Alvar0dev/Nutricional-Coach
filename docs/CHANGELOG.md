# CHANGELOG - Cuaderno de Bitácora SDD

Fecha actual: 2026-10-16
Metodología: Spec Driven Development (SDD) — OpenAPI 3.0

---

## Día 1 - Análisis Inicial

### Logros
- Exploración de la estructura del proyecto existente.
- Revisión de `README.md`, `docs/ARCHITECTURE_SDD.md`, `docs/openapi.yaml`.
- Identificación del stack tecnológico: Spring Boot 3.4, Java 21, Angular 18, OpenPDF, Hibernate JPA.

### ❌ Dificultad Encontrada
**Problema:** La carpeta `spring-petclinic/` generó confusión inicial. Parecía ser parte integral del proyecto Nutricional-Coach, pero es un subproyecto independiente con su propio `.git` interno. Esto provocó que se confundiera el alcance del análisis inicial.

**Solución:** Se separó claramente en `AGENTS.md` y en `README.md` (sección "Organización del Proyecto") con una nota explícita: `spring-petclinic/` es un subproyecto referencia, no parte activa del flujo de negocio del coach nutricional. Se creó `.gitignore` para excluirlo de futuras operaciones de build.

---

## Día 2 - Definición de Contexto Operativo

### Logros
- Creación de `AGENTS.md` con resumen del proyecto, arquitectura, comandos principales y convenciones.
- Creación de `docs/constitution.md` con 6 principios innegociables: simplicidad de stack, separación lógica-interfaz, alineación especificación-código, testing obligatorio, gestión de errores consistente y persistencia controlada.

### ❌ Dificultad Encontrada
**Problema:** Al redactar `AGENTS.md`, no se definieron claramente los límites de edición. Se incluyeron carpetas como `docs/` como "no tocar", pero luego se descubrió que precisamente `docs/` es donde se debe documentar el SDD. Esto generó una contradicción en la primera versión del archivo.

**Solución:** Se corrigió `AGENTS.md` para diferenciar entre "documentación técnica (editable para el proceso SDD)" y "documentación de referencia oficial (ARCHITECTURE_SDD.md, openapi.yaml) que requiere autorización para cambios estructurales". Se añadió una nota clara: el contexto operativo no es un bloqueo, es una guía de responsabilidad.

---

## Día 3 - Documentación SDD y Organización del Proceso

### Logros
- Creación de `docs/CHANGELOG.md` con registro del proceso día a día.
- Actualización de `README.md` con la sección "Organización del Proyecto" y referencia explícita a la metodología SDD.
- Confirmación de los límites de edición y política modular.

### ❌ Dificultad Encontrada (Crítica)
**Problema:** Al realizar el commit con `git commit`, el repositorio tenía un `rebase interactivo` en curso (`rebase in progress`) debido a commits previos del usuario. Esto provocó que el archivo `README.md` entrara en conflicto (`both added`) y que, al abortar el rebase, se perdieran los archivos recién creados (`AGENTS.md`, `docs/constitution.md`, `docs/CHANGELOG.md`).

**Impacto:** Se perdieron 3 archivos nuevos y el trabajo del día 3 tuvo que ser recreado manualmente.

**Solución Implementada:**
1. Se abortó el rebase (`git rebase --abort`) para recuperar control.
2. Se recrearon los archivos desde la memoria del análisis inicial.
3. Se realizó un commit independiente (`git commit -m "Día 3 - Documentación..."`) sin intentar interactuar con el rebase pendiente.
4. Se documentó el incidente en `CHANGELOG.md` como lección aprendida: "Nunca realizar cambios críticos en repositorios con rebase en curso sin verificar `git status` previamente".

---

## Día 4 - Completar Especificación OpenAPI

### Logros
- Completada la especificación `docs/openapi.yaml`: añadidos endpoints `DELETE /api/v1/pacientes/{id}`, `GET /api/v1/recordatorios`, `GET /api/v1/revisiones`, `GET /api/v1/revisiones/{id}`.
- Añadidos schemas `ErrorResponse` y `ErrorDTO` para manejo de errores consistente con código HTTP 404/500.
- Confirmación del commit `6217ec6` y push a `origin/main`.

### ❌ Dificultad Encontrada
**Problema:** Al revisar la spec OpenAPI existente, se detectó que faltaban los endpoints críticos para el flujo completo del negocio: la eliminación de pacientes (requisito de GDPR/protección de datos médicos) y la consulta completa de revisiones (necesaria para la trazabilidad mensual del paciente). Además, no existía un schema de error estándar, lo que provocaría respuestas inconsistentes entre el backend y el frontend Angular cuando ocurra un error.

**Solución Implementada:**
1. Se añadió `DELETE /api/v1/pacientes/{id}` con respuesta `204` (No Content) siguiendo RESTful practices.
2. Se añadieron los endpoints `GET /api/v1/revisiones` y `GET /api/v1/revisiones/{id}` para completar el ciclo de revisión mensual.
3. Se creó `ErrorResponse` como schema base para todos los errores futuros, asegurando que el frontend Angular pueda manejar respuestas de error de forma uniforme sin depender de la estructura del mensaje de la excepción Java.
4. Se documentó la regla en `docs/constitution.md`: "Alineación Especificación-Código" — cualquier cambio en la API debe reflejarse primero en `openapi.yaml`.

---

## Estado Actual del Repositorio
- Rama: `main`
- Último commit: `6217ec6` (Día 4 - Completa especificación OpenAPI)
- Estado: Limpio (`git status` sin conflictos)
- Push: Confirmado (`origin/main` actualizado)
- Archivos críticos creados/actualizados:
  - `AGENTS.md`
  - `docs/constitution.md`
  - `docs/CHANGELOG.md`
  - `docs/openapi.yaml`
  - `README.md`

---

## Próximos Pasos (Día 5+)
1. Validar que la spec `openapi.yaml` esté sincronizada con el código Java existente.
2. Añadir los casos de test correspondientes a los nuevos endpoints (`DELETE`, `GET /revisiones`).
3. Documentar el primer caso de prueba de integración del flujo completo: Alta → Medición → Revisión → PDF.
4. Revisar `spring-petclinic/` para confirmar que no hay interferencia con el build de Maven del proyecto principal.
