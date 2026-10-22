# CHANGELOG - Cuaderno de Bitácora SDD

Fecha actual: 2026-10-20
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

## Día 6 — Continuación ODD (nuevo feature)
- Creado `odd/tasks/medicion-biometrica.md` con S1-S5, vinculando `POST /api/v1/pacientes/{id}/mediciones` a `openapi.yaml`.
- Riesgo nombrado: item 1 (datos biométricos irreversibles). Mitigación: inmutabilidad + validación `min: 0`; referencias a `AGENTS.md`, `constitution.md`, no duplicadas.
- Workspace artifacts (`.config/`, `.gemini/`, `opencode.json`) mantenidos untracked tras `gentle-ai sync --scope=workspace`; no entran al commit.

## Estado Actual del Repositorio
- Rama: `main`
- Último commit: `16d050d` (Día 5 — Fix de Toolchain y Documentación)
- Estado: Limpio (`git status` sin conflictos)
- Push: Confirmado (`origin/main` actualizado)
- Archivos críticos creados/actualizados:
  - `AGENTS.md`
  - `docs/constitution.md`
  - `docs/CHANGELOG.md`
  - `docs/openapi.yaml`
  - `README.md`
  - `SETUP.md`

---

## Próximos Pasos (Día 6+)
1. Validar que la spec `openapi.yaml` esté sincronizada con el código Java existente.
2. Añadir los casos de test correspondientes a los nuevos endpoints (`DELETE`, `GET /revisiones`).
3. Documentar el primer caso de prueba de integración del flujo completo: Alta → Medición → Revisión → PDF.
4. Revisar `spring-petclinic/` para confirmar que no hay interferencia con el build de Maven del proyecto principal.
5. Reiniciar VS Code y verificar que la extensión Java detecte el JDK 17 (autocompletado y depuración).
6. Decidir si `spring-petclinic/` mantiene Java 17 o se alinea a Java 21 (requiere actualizar toolchain, `pom.xml` y CI).

---

## Día 5 — Fix de Toolchain y Documentación

### Logros
- Identificado y corregido el error de build de Gradle (`FAILURE: Cannot find a Java installation... languageVersion=17`): instalado Eclipse Temurin JDK 17 (v17.0.20.101-hotspot) vía `winget`.
- Verificado build completo de `spring-petclinic` con Gradle 9.5.1: `BUILD SUCCESSFUL` en ~41s (21 tareas ejecutadas, 0 errores).
- Eliminado el warning AOT (`unknown enum constant Status.STABLE` / `org.apiguardian.api.API$Status`) añadiendo `compileOnly "org.apiguardian:apiguardian-api:1.1.2"` al `build.gradle` del subproyecto.
- Creado `SETUP.md` con instrucciones de instalación, verificación (`java -version`, `./gradlew build`) y notas del subproyecto independiente.
- Actualizado `docs/CHANGELOG.md` (cuaderno de bitácora SDD) con registro del día y actualización del estado del repositorio.
- Confirmado push (`16d050d` → `origin/main`).

---

## Día 6 - Pruebas, Documentación y Commit

### Logros
- Actualización de `docs/CHANGELOG.md`: corrección del commit hash (`e2f3d7e` → `16d050d`), actualización de la fecha, y entrada Día 6 con el avance de tests y documentation.
- Mejorada `SETUP.md`: se añadieron instrucciones explícitas para correr los tests de `spring-petclinic` vía Gradle (comandos, options y consideraciones de base).
- Verificado que el flujo de build/test funciona con JDK 17 (`./gradlew build` y `./gradlew test` en `spring-petclinic/`, BUILD SUCCESSFUL).

### ❌ Dificultad Encontrada
**Problema:** No existía documentación sobre cómo ejecutar los tests en `spring-petclinic`, lo que impidió reproducibilidad en nuevos entornos.

**Solución Implementada:**
1. Se añadieron secciones completas a `SETUP.md` con los comandos `./gradlew build` e `./gradlew test`, options adicionales (ej. `--no-daemon`, `-Pdb=hsqldb`) y los archivos de tests existentes listados.
2. Se registró en el cuaderno la resolución para futuras sesiones.


### ❌ Dificultad Encontrada
**Problema:** El entorno de trabajo (`G:\Proyectos OpenCode\BBVA\Nutritional Coach`) contenía solo Java 11 y Java 21, sin Java 17. El subproyecto `spring-petclinic/` fija explícitamente `languageVersion = JavaLanguageVersion.of(17)` en `build.gradle` (y `<java.version>17</java.version>` en `pom.xml`). Esto bloqueaba cualquier build, test o compilación. Adicionalmente, aparecía el warning `Status.STABLE` en `:compileAotTestJava` debido a la ausencia del artifact `apiguardian-api` en el classpath de compilación AOT.

**Solución:**
1. Instalación de JDK 17 con `winget install EclipseAdoptium.Temurin.17.JDK --accept-source-agreements --accept-package-agreements`, verificación con `java -version` y actualización temporal de `JAVA_HOME`/`PATH` para la sesión.
2. Aplicación del fix AOT en `spring-petclinic/build.gradle` (sin modificar código fuente de la aplicación; solo configuración de build y una dependencia de compilación).
3. Documentación del proceso en `SETUP.md` y registro en el cuaderno (`docs/CHANGELOG.md`). El cambio de `spring-petclinic/` queda aplicado localmente; no se sube a `spring-projects/spring-petclinic` porque es un gitlink independiente.

---
