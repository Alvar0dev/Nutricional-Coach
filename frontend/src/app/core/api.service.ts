import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_BASE_URL } from './api.config';

/**
 * Servicio base de HTTP para consumir la API del backend Nutricional Coach.
 * Todas las peticiones se resuelven contra http://localhost:8080/api/v1.
 */
@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly base = API_BASE_URL;

  constructor(private readonly http: HttpClient) {}

  get<T>(path: string) {
    return this.http.get<T>(`${this.base}${path}`);
  }

  post<T>(path: string, body?: unknown) {
    return this.http.post<T>(`${this.base}${path}`, body);
  }

  patch<T>(path: string, body?: unknown) {
    return this.http.patch<T>(`${this.base}${path}`, body);
  }

  delete(path: string) {
    return this.http.delete(`${this.base}${path}`);
  }
}
