/**
 * DTO de medición biométrica alineado a la especificación OpenAPI 3.0 (docs/openapi.yaml).
 * Los campos id y pacienteId son generados por el backend y solo se devuelven en respuestas.
 * Reglas de validación: pesoKg >= 0 y grasaPct >= 0.
 */
export interface MedicionBiometricaDTO {
  id?: string;
  pacienteId?: string;
  fecha: string;
  pesoKg: number;
  grasaPct: number;
  masaMuscularKg?: number;
  cinturaCm?: number;
}
