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

## Pruebas (Tests)
- Desde la carpeta `spring-petclinic/`: `./gradlew test` (usa Gradle Wrapper con Java 17).
- Tests existentes en `src/test/java/org/springframework/samples/petclinic/`: `PetClinicIntegrationTests`, `PetControllerTests`, `ClinicServiceTests`, `ValidatorTests`, etc.
- Si se requiere tests sin base de datos externa: `./gradlew test -Pdb=hsqldb`.
- El subproyecto debe mantenerse con Java 17 (`languageVersion = 17`) por ahora; alineación a Java 21 requiere actualizar `build.gradle` y `pom.xml` (pendiente, ver Día 6 Próximos Pasos).
