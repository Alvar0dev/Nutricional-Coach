# Configuración de Entorno — JDK 17 (Spring PetClinic / Gradle)

Fecha: 2026-10-19
Estado: Resuelto

## Problema
Gradle 9.5.1 / `spring-petclinic` requería Java 17 (`languageVersion = 17` en `build.gradle`), pero la máquina solo tenía Java 11 y Java 21 instalados.

## Solución aplicada
- Instalar Eclipse Temurin JDK 17 vía `winget` (v17.0.20.101-hotspot).
- Configurar `JAVA_HOME` y `PATH` para apuntar al nuevo JDK.
- Verificar con `java -version` y `./gradlew build` (BUILD SUCCESSFUL).

## Notas
- El subproyecto `spring-petclinic/` es independiente (gitlink a spring-projects); el cambio de `compileOnly 'org.apiguardian:apiguardian-api:1.1.2'` está aplicado localmente para eliminar el warning `Status.STABLE` en `compileAotTestJava`.
- Si se recrea el entorno, repetir: `winget install EclipseAdoptium.Temurin.17.JDK`.
