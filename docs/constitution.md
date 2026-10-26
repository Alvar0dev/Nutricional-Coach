# Constitution of Nutricional-Coach

## 1. Simplicidad de Stack
El proyecto mantiene un stack mínimo y consistente: Java 21 + Spring Boot 3.4 + Maven + Hibernate JPA + Angular 18 + OpenPDF. No se introducen tecnologías adicionales sin un caso de uso específico y aprobación explícita.

## 2. Separación Estricta Lógica-Interfaz
La lógica de negocio (tratamiento de pacientes, mediciones, alertas, generación de PDF) reside exclusivamente en el backend (Spring Boot). El frontend (Angular/Thymeleaf) solo se encarga de la presentación. No se mezclan modelos de dominio con componentes UI.

## 3. Alineación Especificación-Código
Toda nueva funcionalidad debe reflejar cambios en la especificación OpenAPI (`openapi.yaml`) Los cambios en la API deben propagarse primero a la documentación antes de implementar.

## 4. Testing como Requisito Obligatorio
Todos los cambios deben incluir pruebas unitarias y de integración. El 80% de los cambios deben tener al menos un test que pase. Sin test, el cambio no puede ser aprobado.

## 5. Gestión de Errores Consistente
Los errores deben ser capturados, logueados y devueltos con códigos HTTP apropiados (4xx/5xx). No se exponen excepciones internas al cliente. Todo error debe seguir el patrón de respuesta definido en la API.

## 6. Persistencia de Datos Controlada
Las operaciones críticas (creación de pacientes, registro de mediciones, generación de PDFs) se almacenan en la base de datos (RDS) y no en variables de entorno temporales. Las sesiones y estado transaccional se gestionan mediante la capa de persistencia del backend.

## 7. Evolución Modular
Las características se añaden de forma modular, respetando la arquitectura existente. Cada nueva funcionalidad debe tener su propia ruta de ejecución y pruebas independientes.
