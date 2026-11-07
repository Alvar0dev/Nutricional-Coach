/**
 * DTO de paciente alineado a la especificación OpenAPI 3.0 (docs/openapi.yaml).
 * Los campos id y fechaAlta son generados por el backend y solo se devuelven en respuestas.
 */
export interface PacienteDTO {
  id?: string;
  nombre: string;
  apellidos: string;
  email: string;
  telefono?: string;
  objetivo?: string;
  fechaAlta?: string;
}
