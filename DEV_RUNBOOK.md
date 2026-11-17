# DEV_RUNBOOK.md — Comandos ejecutables y entornos

Estado: 2026-11-17 (Día 21). Backend Spring Boot 3.4 + H2 (local) / PostgreSQL RDS (`prod`, `application-prod.properties`) y Frontend Angular 18 operativos.

## Entorno requerido

- Java 21 (`C:\Users\alvar\bin\mvn21.cmd` — proyecto principal) / Java 17 (subproyecto `spring-petclinic/` — ver `SETUP.md`)
- Maven 3.9.9 vía wrapper `C:\Users\alvar\bin\mvn21.cmd` (raíz) y Node.js / npm en `frontend/`
- `.env` (ignorado en git, plantilla en `.env.example`): variables para conectividad local/Cloud RDS y S3 (`DB_URL`, `DB_DRIVER`, `DB_USER`, `DB_PASSWORD`, `AWS_REGION`, `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_S3_BUCKET`), cargadas con `spring.config.import=optional:file:.env[.properties]` y fallback por defecto a H2 en memoria.
- Base de datos local por defecto: H2 en memoria (`jdbc:h2:mem:nutricionaldb`), consola disponible en `http://localhost:8080/h2-console`

## Comandos exactos

### Compilar / build

```bash
# Proyecto principal (Backend Spring Boot 3.4 + Java 21)
C:\Users\alvar\bin\mvn21.cmd clean compile

# Frontend Angular 18
cd frontend
npx tsc --noEmit -p tsconfig.app.json
```

### Levantar local

```bash
# Backend en http://localhost:8080 (y consola H2 en http://localhost:8080/h2-console)
C:\Users\alvar\bin\mvn21.cmd spring-boot:run

# Backend con datos semilla de demo (perfil local: paciente, mediciones, revisión + PDF y recordatorio pendiente)
C:\Users\alvar\bin\mvn21.cmd spring-boot:run -Dspring-boot.run.profiles=local

# Frontend Angular 18 en http://localhost:4200
cd frontend
npm start
```

### Suite de tests

```bash
# Backend — suite completa (45/45 verdes, incluye E2E H2, placeholders .env, DataInitializerTest y ProdProfileConfigTest)
C:\Users\alvar\bin\mvn21.cmd test

# Frontend — suite completa en ChromeHeadless (14/14 verdes)
cd frontend
npm test -- --watch=false --browsers=ChromeHeadless
```

### Linter / verificación estática

```bash
# Java (cuando build esté disponible)
# mvn spotless:check   # o equivalente con formatter
# mvn checkstyle:check

# OpenAPI / spec
# Validación de `docs/openapi.yaml` con swagger-cli o Swagger Editor online
```

## Entornos de ejecución

| Entorno | Propósito | Cómo entrar |
|---------|-----------|-------------|
| `local` | Desarrollo local | `mvn21.cmd spring-boot:run` (o `-Dspring-boot.run.profiles=local` para datos semilla) + `npm start` en `frontend/` |
| `dev` / `prod` | AWS EC2 + RDS (PostgreSQL) | Driver PostgreSQL + `application-prod.properties` listos (`-Dspring-boot.run.profiles=prod`), pendiente guía de despliegue EC2 |
| `test` | Pruebas con datos mínimos | H2 / HSQLDB (`-Pdb=hsqldb` en subproyecto) |

## Gotchas / peculiaridades conocidas

- **Java version conflict**: `spring-petclinic/` requiere Java 17 (`languageVersion = 17`); la máquina tiene 21 y 17. Si se corre `gradlew` con JAVA_HOME=21, falla el build del subproyecto. Ver `SETUP.md` para solución (`winget install EclipseAdoptium.Temurin.17.JDK`).
- **Rebase pendiente**: El repositorio tuvo un `rebase interactive` en curso en días anteriores (conflicto `README.md`). Resolver con `git status` antes de cualquier `git commit` o `push`.
- **Subproyecto independiente**: `spring-petclinic/` tiene `.git` propio (gitlink). No debe incluirse en builds del proyecto principal sin verificar que no haya conflictos de módulos.
- **Sin `pom.xml` raíz para Nutricional-Coach**: El control `PacienteController.java` fue creado en `src/` pero aún no hay `pom.xml` que lo compile. Si se intenta `mvn test` desde raíz hoy, fallará. Configurar Módulo principal es paso pendiente.
- **OpenAPI como fuente de verdad**: Cualquier cambio en endpoint debe reflejarse primero en `docs/openapi.yaml` (regla `constitution.md` §3). No modificar `.java` sin actualizar la especificación.
- **Soft-delete obligatorio**: `PacienteController.deletePaciente()` debe usar `PacienteService.softDeletePaciente()` (no `deleteById`). Violar esto es un error de datos (risk item 1).
- **`.atl/skill-registry.md`**: Actualizado automáticamente; no editar manualmente sin usar `gentle-ai skill-registry refresh`.
