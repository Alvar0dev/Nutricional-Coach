import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../api.config';
import { PacienteDTO } from '../models/paciente.dto';

/**
 * Servicio de pacientes que consume la API del backend Nutricional Coach.
 * Endpoints: POST /pacientes, GET /pacientes, GET /pacientes/{id}.
 */
@Injectable({ providedIn: 'root' })
export class PacienteService {
  private readonly base = `${API_BASE_URL}/pacientes`;

  constructor(private readonly http: HttpClient) {}

  /** Lista solo pacientes activos con paginación page/size (GET /pacientes). */
  listar(page = 0, size = 10): Observable<PacienteDTO[]> {
    return this.http.get<PacienteDTO[]>(`${this.base}?page=${page}&size=${size}`);
  }

  /** Obtiene un paciente por ID (GET /pacientes/{id}). */
  obtenerPorId(id: string): Observable<PacienteDTO> {
    return this.http.get<PacienteDTO>(`${this.base}/${id}`);
  }

  /** Da de alta un paciente (POST /pacientes). Devuelve 201 Created + Location. */
  crear(paciente: Omit<PacienteDTO, 'id' | 'fechaAlta'>): Observable<PacienteDTO> {
    return this.http.post<PacienteDTO>(this.base, paciente);
  }
}
