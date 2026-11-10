/**
 * DTO de revisión mensual alineado con el schema RevisionDTO de docs/openapi.yaml.
 */
export interface RevisionDTO {
  id?: string;
  pacienteId: string;
  medicionAnteriorId?: string;
  medicionActualId?: string;
  deltaPeso?: number;
  deltaGrasa?: number;
  deltaMusculo?: number;
  comentariosAjuste?: string;
  ajustesDieta?: string;
  ajustesEntreno?: string;
  pdfUrl?: string;
  fechaRevision?: string;
}
