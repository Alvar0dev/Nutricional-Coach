# Plan de Continuación — Nutricional Coach

> Documento operativo para retomar el trabajo en futuras sesiones sin re-analizar ni re-planificar desde cero.  
> Referencia técnica de infraestructura: [`aws Tutorial.md`](aws%20Tutorial.md) · Mapa documental ODD: [`docs/organizacion-documentacion.md`](docs/organizacion-documentacion.md).

---

## 1. Estado base al finalizar TODO 1, TODO 2 (código) y TODO 3 (verificado 2026-11-28)

- **Rama actual:** `feat/s3-sdk-integration` (creada desde `main` `1340009`; pendiente de push + PR #7 a `main`).
- **Artefactos vigentes:**
  - Backend JAR: `target/nutricional-coach-1.0.0-SNAPSHOT.jar` (Java 21, `51/51` tests verificados en Día 28 con `mvn21.cmd clean package` + `odd-verifier` `PASS`).
  - Frontend Angular 18: `frontend/dist/frontend/browser/` (`API_BASE_URL = '/api/v1'`, `15/15` tests verificados en Día 23).
- **TODO 1 — Validación E2E del Backend `prod` en Emulador Local ($0): COMPLETADO Y VERIFICADO.**
- **TODO 2 — Integración SDK real AWS S3 (`software.amazon.awssdk:s3` `2.31.78`): COMPLETADO EN RAMA `feat/s3-sdk-integration` (PENDIENTE PR #7).**
  - `S3ClientConfig` (`@ConditionalOnProperty("aws.s3.enabled")`) soporta LocalStack (`AWS_S3_ENDPOINT=http://localhost:14566`, `AWS_S3_PATH_STYLE_ACCESS=true`, credenciales explícitas) y EC2 AWS real (IAM Role vía `DefaultCredentialsProvider` sin credenciales estáticas).
  - Probado en vivo contra LocalStack S3 (`s3://nutricional-coach-pdfs/revisiones/revision-8694689a-03e2-4650-9882-4a5570495198.pdf`, `1079` bytes, `ContentType: application/pdf`, recarga idéntica en segunda petición).
- **TODO 3 — Limpieza y alineación base ODD: COMPLETADO (2026-11-28).**

> **Nota operativa al iniciar cualquier nueva sesión:** si hubo reinicio de PC o del servidor de sesión, `//./pipe/docker_engine` puede quedar detenido y LocalStack Community reinicia buckets en memoria. Comprobar primero `docker compose ps` desde `C:\lab-aws-nutricional\`, abrir Docker Desktop si hace falta, ejecutar `docker compose up -d` y verificar/recrear el bucket (`docker exec localstack_s3 awslocal s3 mb s3://nutricional-coach-pdfs --region eu-west-1`).

---

## 2. Próximos TODOs (orden recomendado)

### TODO 2 — Integrar SDK real de AWS S3 (`software.amazon.awssdk:s3`) `[IMPLEMENTADO EN feat/s3-sdk-integration — PENDIENTE PR #7]`
- **Resultado verificado (`2026-11-28`):** `pom.xml` (`2.31.78`), `S3ClientConfig`, `S3PdfStorageService`, `PdfStorageServiceTest`, `51/51` tests verdes, subida/lectura real en LocalStack S3 y dictamen `PASS` de `odd-verifier` (`Risk: item 5`).
- **Pendiente de entrega:** push de `feat/s3-sdk-integration` → abrir/mergear **PR #7** a `main` y sincronizar `main` localmente.

---

### TODO 3 — Limpieza y alineación base ODD (documentación) `[COMPLETADO 2026-11-28]`
- **Objetivo:** Eliminar contradicciones históricas detectadas entre documentos tras completar el MVP y la preparación cloud.
- **Resultado:** `PROJECT_INDEX.md` y `odd/task/estado-actual-proyecto.md` actualizados al estado post-MVP, `PLAN_PENDIENTE_MVP.md` congelado como histórico MVP y `plan.md` añadido al mapa en `docs/organizacion-documentacion.md`.

---

### TODO 4 — Despliegue real en AWS Free Tier
- **Objetivo:** Pasar el stack validado del emulador local a una cuenta real de AWS (`eu-west-1`) siguiendo la **Parte 2** de `aws Tutorial.md`.
- **Riesgo:** `Risk: item 2 y 5` (seguridad, red, credenciales y costes). Nunca commitear archivos `.pem`, contraseñas RDS ni access keys.
- **Secuencia estricta:**
  1. **Fase 0 — Presupuesto:** crear AWS Budget (`$1.00 USD`) y fijar región `eu-west-1`.
  2. **Fase 1 — Security Groups:** `nutricional-coach-ec2-sg` (`22` solo `My IP`, `80` y `443` abiertos) y `nutricional-coach-rds-sg` (`5432` solo desde `nutricional-coach-ec2-sg`).
  3. **Fase 2 — Amazon RDS PostgreSQL:** `db.t3.micro` / `db.t4g.micro`, `20 GiB`, *storage autoscaling* desactivado, **`Public access: No`**, base `nutricionaldb`.
  4. **Fase 3 — Amazon S3 privado + IAM Role:** crear bucket privado (`Block all public access`) y asociar IAM Role acotado a la EC2 (si se completó TODO 2).
  5. **Fase 4–6 — EC2 + Nginx + `systemd`:** Ubuntu 24.04 LTS, crear `2 GB` swap, instalar `openjdk-21-jre-headless` y `nginx`, transferir `target/nutricional-coach-1.0.0-SNAPSHOT.jar` y `frontend/dist/frontend/browser/`, arrancar `systemd` con `PROD_JPA_DDL_AUTO=update` → cambiar a `validate`, y ejecutar smoke test desde `http://<EC2_PUBLIC_IP>/`.

---

## 3. Cómo reflejar cada TODO en la documentación

| Documento | Qué registrar exactamente | Cuándo editar |
|---|---|---|
| `todo` | Checklist corto de la sesión actual | En cada cierre de sesión |
| `TODO.md` | Backlog activo post-MVP | Al completar cada TODO |
| `docs/CHANGELOG.md` | Resumen por hito: *Cambios / Dificultades / Por qué / Riesgo* (sin repetir comandos largos) | Al cerrar jornada o PR |
| `aws Tutorial.md` | Solo ajustes técnicos reales (en TODO 2: quitar aviso de S3 en memoria y documentar `AWS_S3_ENDPOINT` + `AWS_S3_PATH_STYLE_ACCESS`) | Cuando cambie un paso o parámetro real |
| `DEV_RUNBOOK.md` | Comandos, nuevas variables de entorno y gotchas vigentes | En TODO 2 (vars S3) y TODO 4 (entorno prod) |
| `PROJECT_INDEX.md` y `odd/task/estado-actual-proyecto.md` | Arquitectura real y estado base ODD | En TODO 3 (limpieza) y tras TODO 2 / TODO 4 |
| `PLAN_PENDIENTE_MVP.md` | Histórico MVP | Congelar en TODO 3 |
| `docs/openapi.yaml` y `README.md` | Contratos REST y bitácora histórica | **No tocar** (no cambian endpoints) |
| **Engram** (`mem_save` + `mem_session_summary`) | Decisiones, gotchas no obvios y resumen de cierre | Tras hitos relevantes y al cerrar cada sesión |

---

## 4. Invariantes y reglas del proyecto (no romper)

- **Archivos intocables:** no modificar ni commitear `opencode.json` ni el submódulo `spring-petclinic/` (Java 17 + Gradle).
- **Java 21 explícito:** usar siempre `C:\Users\alvar\bin\mvn21.cmd` para Maven y `C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot\bin\java.exe` para ejecutar el JAR (el `java` del `PATH` apunta a JDK 17).
- **Soft-delete obligatorio:** nunca eliminar físicamente registros en `pacientes` (`activo = false` + `fechaBaja`).
- **LocalStack en Windows:** mantener `localstack/localstack:4.14.0` y mapeo `14566:4566` en `C:\lab-aws-nutricional\docker-compose.yml` para evitar `LOCALSTACK_AUTH_TOKEN` (`exit code 55`) y rangos TCP excluidos de WinNAT.
- **PowerShell 5.1:** no usar `&&` (usar `$LASTEXITCODE -eq 0`), usar el pipeline sin comillas en Git Bash para `git credential fill` sin imprimir tokens, y validar salidas vacías explícitamente en lugar de `-notmatch` directo.
