# DEV_RUNBOOK.md — Comandos ejecutables y entornos

Estado: 2026-10-22. Proyecto en fase ODD; build root pendiente.

## Entorno requerido

- Java 21 (proyecto principal — planeado) / Java 17 (subproyecto `spring-petclinic/` — ver `SETUP.md`)
- Maven (`mvn`) o Gradle (`gradlew`) — hoy no configurado en raíz; solo `spring-petclinic/gradlew` disponible
- `.env` (no creado aún): variables requeridas para conectividad futura con RDS y S3. Plantilla mínima: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `AWS_REGION`, `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`
- Docker Compose (`docker-compose.yml` en `spring-petclinic/` — referencia de servicios locales; no es obligatorio para Nutricional-Coach hoy)

## Comandos exactos

### Compilar / build

```bash
# Proyecto principal (cuando se configure build root — pendiente)
# mvn clean compile
# mvn spring-boot:run

# Subproyecto referencia (Java 17 — ver SETUP.md)
cd spring-petclinic
.
/gradlew build
```

### Levantar local (cuando el build esté configurado)

```bash
# Backend (Spring Boot — pendiente configuración raíz)
# mvn spring-boot:run
# o con perfil local: java -jar target/*.jar --spring.profiles.active=local

# Frontend (Angular 18 — pendiente)
# npm run dev
```

### Suite de tests

```bash
# Subproyecto existente (Java 17, Gradle Wrapper)
cd spring-petclinic
.
/gradlew test              # suite completa
./gradlew test -Pdb=hsqldb  # sin base de datos externa

# Proyecto principal — tests recientes creados (pending build root)
# mvn test
# mvn test -Dtest=PacienteControllerTest
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
| `local` | Desarrollo local | `mvn spring-boot:run` + `npm run dev` (pendiente) |
| `dev` | Integración temprana | AWS EC2 (Free Tier) — planeado, no configurado |
| `test` | Pruebas con datos mínimos | H2 / HSQLDB (`-Pdb=hsqldb` en subproyecto) |

## Gotchas / peculiaridades conocidas

- **Java version conflict**: `spring-petclinic/` requiere Java 17 (`languageVersion = 17`); la máquina tiene 21 y 17. Si se corre `gradlew` con JAVA_HOME=21, falla el build del subproyecto. Ver `SETUP.md` para solución (`winget install EclipseAdoptium.Temurin.17.JDK`).
- **Rebase pendiente**: El repositorio tuvo un `rebase interactive` en curso en días anteriores (conflicto `README.md`). Resolver con `git status` antes de cualquier `git commit` o `push`.
- **Subproyecto independiente**: `spring-petclinic/` tiene `.git` propio (gitlink). No debe incluirse en builds del proyecto principal sin verificar que no haya conflictos de módulos.
- **Sin `pom.xml` raíz para Nutricional-Coach**: El control `PacienteController.java` fue creado en `src/` pero aún no hay `pom.xml` que lo compile. Si se intenta `mvn test` desde raíz hoy, fallará. Configurar Módulo principal es paso pendiente.
- **OpenAPI como fuente de verdad**: Cualquier cambio en endpoint debe reflejarse primero en `docs/openapi.yaml` (regla `constitution.md` §3). No modificar `.java` sin actualizar la especificación.
- **Soft-delete obligatorio**: `PacienteController.deletePaciente()` debe usar `PacienteService.softDeletePaciente()` (no `deleteById`). Violar esto es un error de datos (risk item 1).
- **`.atl/skill-registry.md`**: Actualizado automáticamente; no editar manualmente sin usar `gentle-ai skill-registry refresh`.
