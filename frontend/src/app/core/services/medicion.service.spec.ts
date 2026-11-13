import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { API_BASE_URL } from '../api.config';
import { MedicionBiometricaDTO } from '../models/medicion.dto';
import { MedicionService } from './medicion.service';

describe('MedicionService (S5, S6)', () => {
  let service: MedicionService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), MedicionService]
    });
    service = TestBed.inject(MedicionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('S5: registrar() envía POST /pacientes/{id}/mediciones y recibe 201', () => {
    const entrada: Omit<MedicionBiometricaDTO, 'id' | 'pacienteId'> = {
      fecha: '2026-11-10',
      pesoKg: 75.5,
      grasaPct: 14.2,
      masaMuscularKg: 36.1
    };
    const creada: MedicionBiometricaDTO = {
      ...entrada,
      id: 'm-1',
      pacienteId: 'p-1'
    };

    service.registrar('p-1', entrada).subscribe((res) => {
      expect(res).toEqual(creada);
    });

    const req = httpMock.expectOne(`${API_BASE_URL}/pacientes/p-1/mediciones`);
    expect(req.request.method).toBe('POST');
    req.flush(creada, { status: 201, statusText: 'Created' });
  });

  it('S6: listarPorPaciente() envía GET /pacientes/{id}/mediciones y maneja 200 y 404', () => {
    const lista: MedicionBiometricaDTO[] = [
      { id: 'm-1', pacienteId: 'p-1', fecha: '2026-11-10', pesoKg: 75.5, grasaPct: 14.2 }
    ];

    service.listarPorPaciente('p-1').subscribe((res) => {
      expect(res.length).toBe(1);
      expect(res[0].id).toBe('m-1');
    });
    const req200 = httpMock.expectOne(`${API_BASE_URL}/pacientes/p-1/mediciones`);
    expect(req200.request.method).toBe('GET');
    req200.flush(lista, { status: 200, statusText: 'OK' });

    let statusError = 0;
    service.listarPorPaciente('p-404').subscribe({
      next: () => fail('Debería fallar con 404'),
      error: (err: HttpErrorResponse) => {
        statusError = err.status;
      }
    });
    const req404 = httpMock.expectOne(`${API_BASE_URL}/pacientes/p-404/mediciones`);
    req404.flush('Not found', { status: 404, statusText: 'Not Found' });
    expect(statusError).toBe(404);
  });
});
