import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { API_BASE_URL } from '../api.config';
import { RecordatorioDTO } from '../models/recordatorio.dto';
import { RecordatorioService } from './recordatorio.service';

describe('RecordatorioService (S10, S11)', () => {
  let service: RecordatorioService;
  let httpMock: HttpTestingController;
  const baseUrl = `${API_BASE_URL}/recordatorios`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), RecordatorioService]
    });
    service = TestBed.inject(RecordatorioService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('S10: listarPendientes() envía GET /recordatorios y recibe 200 con lista', () => {
    const lista: RecordatorioDTO[] = [
      { id: 'rec-1', pacienteId: 'p-1', estado: 'PENDIENTE', tipo: 'REVISION_MENSUAL' }
    ];

    service.listarPendientes().subscribe((res) => {
      expect(res).toEqual(lista);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');
    req.flush(lista, { status: 200, statusText: 'OK' });
  });

  it('S11: actualizarFeedback() envía PATCH /recordatorios/{id} y maneja 200 y 404', () => {
    const actualizado: RecordatorioDTO = {
      id: 'rec-1',
      pacienteId: 'p-1',
      estado: 'PENDIENTE',
      feedbackPaciente: 'Todo bien esta semana'
    };

    service.actualizarFeedback('rec-1', 'Todo bien esta semana').subscribe((res) => {
      expect(res).toEqual(actualizado);
    });

    const req200 = httpMock.expectOne(`${baseUrl}/rec-1`);
    expect(req200.request.method).toBe('PATCH');
    expect(req200.request.body).toEqual({ feedbackPaciente: 'Todo bien esta semana' });
    req200.flush(actualizado, { status: 200, statusText: 'OK' });

    let statusError = 0;
    service.actualizarFeedback('rec-404', 'Texto').subscribe({
      next: () => fail('Debería fallar con 404'),
      error: (err: HttpErrorResponse) => {
        statusError = err.status;
      }
    });
    const req404 = httpMock.expectOne(`${baseUrl}/rec-404`);
    req404.flush('Not found', { status: 404, statusText: 'Not Found' });
    expect(statusError).toBe(404);
  });
});
