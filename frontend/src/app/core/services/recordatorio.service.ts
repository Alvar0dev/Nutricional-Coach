import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../api.config';
import { RecordatorioDTO, RecordatorioFeedbackDTO } from '../models/recordatorio.dto';

/**
 * Servicio de recordatorios que consume la API del backend Nutricional Coach.
 * Endpoints:
 * - GET /recordatorios (S10)
 * - PATCH /recordatorios/{id} (S11)
 */
@Injectable({ providedIn: 'root' })
export class RecordatorioService {
  private readonly base = `${API_BASE_URL}/recordatorios`;

  constructor(private readonly http: HttpClient) {}

  /** Lista los recordatorios pendientes (GET /recordatorios). Responde 200 con lista de RecordatorioDTO. */
  listarPendientes(): Observable<RecordatorioDTO[]> {
    return this.http.get<RecordatorioDTO[]>(this.base);
  }

  /** Actualiza el feedback del paciente de un recordatorio (PATCH /recordatorios/{id}). Responde 200 con RecordatorioDTO o 404. */
  actualizarFeedback(id: string, feedbackPaciente: string): Observable<RecordatorioDTO> {
    const body: RecordatorioFeedbackDTO = { feedbackPaciente };
    return this.http.patch<RecordatorioDTO>(`${this.base}/${id}`, body);
  }
}
