import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../api.config';
import { MedicionBiometricaDTO } from '../models/medicion.dto';

/**
 * Servicio de mediciones biométricas que consume la API del backend Nutricional Coach.
 * Endpoints: POST /pacientes/{id}/mediciones, GET /pacientes/{id}/mediciones.
 */
@Injectable({ providedIn: 'root' })
export class MedicionService {
  constructor(private readonly http: HttpClient) {}

  /** Registra una medición biométrica para un paciente (POST /pacientes/{id}/mediciones). Devuelve 201 Created + Location. */
  registrar(pacienteId: string, medicion: Omit<MedicionBiometricaDTO, 'id' | 'pacienteId'>): Observable<MedicionBiometricaDTO> {
    return this.http.post<MedicionBiometricaDTO>(
      `${API_BASE_URL}/pacientes/${pacienteId}/mediciones`,
      medicion
    );
  }

  /** Lista las mediciones biométricas de un paciente (GET /pacientes/{id}/mediciones). Responde 200 con lista. */
  listarPorPaciente(pacienteId: string): Observable<MedicionBiometricaDTO[]> {
    return this.http.get<MedicionBiometricaDTO[]>(
      `${API_BASE_URL}/pacientes/${pacienteId}/mediciones`
    );
  }
}
