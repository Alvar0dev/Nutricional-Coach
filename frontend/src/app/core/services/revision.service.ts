import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../api.config';
import { RevisionDTO } from '../models/revision.dto';

/**
 * Servicio de revisiones mensuales que consume la API del backend Nutricional Coach.
 * Endpoints:
 * - GET /revisiones (S7)
 * - GET /revisiones/{id} (S7)
 * - POST /pacientes/{id}/revisiones (S8)
 * - GET /revisiones/{id}/pdf (S9)
 */
@Injectable({ providedIn: 'root' })
export class RevisionService {
  private readonly base = `${API_BASE_URL}/revisiones`;

  constructor(private readonly http: HttpClient) {}

  /** Lista todas las revisiones mensuales (GET /revisiones). Responde 200 con lista de RevisionDTO. */
  listar(): Observable<RevisionDTO[]> {
    return this.http.get<RevisionDTO[]>(this.base);
  }

  /** Obtiene una revisión mensual por su ID (GET /revisiones/{id}). Responde 200 o 404. */
  obtenerPorId(id: string): Observable<RevisionDTO> {
    return this.http.get<RevisionDTO>(`${this.base}/${id}`);
  }

  /** Crea una revisión mensual con deltas para un paciente (POST /pacientes/{id}/revisiones). Responde 201 Created + Location. */
  crear(pacienteId: string, revision: Omit<RevisionDTO, 'id'>): Observable<RevisionDTO> {
    return this.http.post<RevisionDTO>(
      `${API_BASE_URL}/pacientes/${pacienteId}/revisiones`,
      { ...revision, pacienteId }
    );
  }

  /** Descarga el PDF del plan de dieta y entreno de una revisión (GET /revisiones/{id}/pdf). Responde 200 application/pdf o 404. */
  descargarPdf(id: string): Observable<Blob> {
    return this.http.get(`${this.base}/${id}/pdf`, { responseType: 'blob' });
  }
}
