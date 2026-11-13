/**
 * DTOs de recordatorios alineados con RecordatorioDTO y RecordatorioFeedbackDTO de docs/openapi.yaml.
 */
export interface RecordatorioDTO {
  id?: string;
  pacienteId: string;
  fechaProgramada?: string;
  tipo?: 'CONTROL' | 'REVISION_MENSUAL' | string;
  estado?: 'PENDIENTE' | 'COMPLETADO' | string;
  feedbackPaciente?: string;
}

export interface RecordatorioFeedbackDTO {
  feedbackPaciente: string;
}
