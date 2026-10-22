# AGENTS.md - Nutricional-Coach

## Resumen del Proyecto
Plataforma cloud de nutrición deportiva y biometría construida con Spring Boot 3.4 (Java 21) y Angular 18. Implementa un flujo de negocio desde alta de pacientes hasta generación de planes de dieta y entrenamiento mediante alertas mensuales y exportación a PDF. Adopta metodología ODD (Organic Driven Development) con arquitectura definida en OpenAPI 3.0.

## Arquitectura Actual y Tecnologías
- **Backend**: Spring Boot 3.4, Java 21, Maven, Hibernate JPA
- **Frontend**: Angular 18, Thymeleaf, OpenPDF (generación de PDFs)
- **Stack**: Monorepo con módulos Java y Angular, desplegado en AWS Free Tier (EC2, RDS, S3)
- **Patrones**: Separación clara entre lógica de negocio (backend) e interfaz (frontend)

## Comandos Principales
- **Ejecución**: `mvn spring-boot:run` (backend), `npm run dev` (frontend)
- **Compilación**: `mvn clean compile` / `gradle build`
- **Tests**: `mvn test` / `gradle test`
- **API**: `curl http://localhost:8080/api/v1/pacientes`
- **Documentación**: `mvn javadoc:javadoc` / `openapi.yaml` (especificación existente)

## Convenciones de Código y Estilo
- **Formato**: Maven + Gradle, Hibernate JPA, Spring Boot
- **Estilo**: Angular 18 con Thymeleaf, OpenPDF para PDFs
- **Testing**: Pruebas unitarias y de integración con Maven/Gradle
- **Configuración**: `.gitignore` excluye `target/`, `node_modules/`, `dist/`, `.angular/`, `.env`

## Límites Claros (Carpetas/Archivos que no tocar sin permiso)
- **No modificar sin autorización**: 
  - `docs/` (documentación oficial)
  - `src/` (código fuente principal)
  - `spring-petclinic/` (subproyecto independiente)
- **Modificar permitido**:
  - `pom.xml`, `build.gradle`, `src/` (código fuente)
  - `openapi.yaml` (actualización de la API)
  - Agregar nuevos endpoints en la documentación
  - `AGENTS.md`, `docs/constitution.md`, `docs/CHANGELOG.md`, `PROJECT_INDEX.md`, `DEV_RUNBOOK.md` (documentos ODD)