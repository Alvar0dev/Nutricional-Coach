# Handoff — Estado tras alinear Ruta/CORS en Backend (Día 17) y Plan para Mañana

## Contexto (2026-11-12)
- **Repo:** `G:\Proyectos OpenCode\BBVA\Nutritional Coach`
- **Rama activa:** `feat/recordatorios` (sincronizada con `origin/feat/recordatorios`)
- **Backend:** Spring Boot 3.4 + Java 21, endpoints en `http://localhost:8080/api/v1`, suite **40/40 tests verdes** (`mvn21.cmd test`).
- **Frontend:** Angular 18 en `frontend/`, `tsc --noEmit` limpio (`tsconfig.app.json` y `tsconfig.spec.json`), **14/14 tests unitarios verdes** en `ChromeHeadless` (`npx ng test --watch=false --browsers=ChromeHeadless`), proxy `:4200 -> :8080` en `proxy.conf.json`.

## Lo completado hoy (Día 17 — 2026-11-12)
1. **Alineación contractual de `POST /api/v1/pacientes/{id}/revisiones` (`RevisionController.java`):** Se cambió `@RequestMapping("/api/v1/revisiones")` a `@RequestMapping("/api/v1")` en la clase y se explicitaron los sub-paths `/revisiones`, `/revisiones/{id}` y `/revisiones/{id}/pdf` en los `@GetMapping`, corrigiendo el 404 que ocurría en `/api/v1/pacientes/{id}/revisiones`.
2. **Habilitación de CORS (`http://localhost:4200`) en los 4 controladores REST:** Se añadió `@CrossOrigin(origins = "http://localhost:4200")` en `PacienteController`, `MedicionBiometricaController`, `RevisionController` y `RecordatorioController`.
3. **Verificación HTTP con `MockMvc` (`RevisionControllerTest.java`):** Nuevo test `rutasHttpYCors_alineadasConOpenApi` con `MockMvcBuilders.standaloneSetup` que verifica el enrutamiento HTTP real (`201 Created`, header `Location`, los tres `GET`) y la cabecera `Access-Control-Allow-Origin: http://localhost:4200`. Suite en **40/40 verdes**.
4. **Documentación integral:** Registradas las dificultades encontradas (falso positivo de llamadas directas en tests unitarios, manejo de preflight `OPTIONS` en `standaloneSetup` y bloqueo de configuración en RDD por falta de modelo en `review-readability`) y las decisiones arquitectónicas en `docs/CHANGELOG.md`, `TODO.md`, `PLAN_PENDIENTE_MVP.md` y `todo`.

## TODOs para mañana (en orden de ejecución)

### 1. Prueba de integración E2E local (`:4200` ↔ `:8080`)
- **Acción:** Verificar/configurar la clase de arranque `@SpringBootApplication` y propiedades locales para levantar simultáneamente backend (`mvn21.cmd spring-boot:run` en `:8080`) y frontend (`cd frontend && npm start` en `:4200`), probando de punta a punta: alta de paciente → medición → revisión con cálculo de deltas → descarga de PDF → recordatorio con feedback.

### 2. Configuración de entorno e infraestructura (`.env` / AWS RDS & S3)
- **Acción:** Crear plantilla `.env.example` y archivo local `.env` (ignorado por Git) con `DB_URL`, `DB_USER`, `DB_PASSWORD`, `AWS_REGION` y credenciales S3.

### 3. Pull Request de `feat/recordatorios` a `main`
- **Acción:** Autenticar GitHub CLI (`gh auth login`) o abrir el Pull Request directamente en GitHub desde `feat/recordatorios` hacia `main`.
