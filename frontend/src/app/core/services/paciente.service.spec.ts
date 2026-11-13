import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { API_BASE_URL } from '../api.config';
import { PacienteDTO } from '../models/paciente.dto';
import { PacienteService } from './paciente.service';

describe('PacienteService (S1, S2, S3, S4)', () => {
  let service: PacienteService;
  let httpMock: HttpTestingController;
  const baseUrl = `${API_BASE_URL}/pacientes`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), PacienteService]
    });
    service = TestBed.inject(PacienteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('S1: crear() envía POST /pacientes y recibe 201 con PacienteDTO', () => {
    const entrada: Omit<PacienteDTO, 'id' | 'fechaAlta'> = {
      nombre: 'Ana',
      apellidos: 'García',
      email: 'ana@example.com'
    };
    const creado: PacienteDTO = { ...entrada, id: 'p-1', fechaAlta: '2026-11-10' };

    service.crear(entrada).subscribe((res) => {
      expect(res).toEqual(creado);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('POST');
    req.flush(creado, { status: 201, statusText: 'Created' });
  });

  it('S2: listar() envía GET /pacientes?page=0&size=10 y recibe 200 con lista', () => {
    const lista: PacienteDTO[] = [
      { id: 'p-1', nombre: 'Ana', apellidos: 'García', email: 'ana@example.com' }
    ];

    service.listar(0, 10).subscribe((res) => {
      expect(res.length).toBe(1);
      expect(res[0].id).toBe('p-1');
    });

    const req = httpMock.expectOne(`${baseUrl}?page=0&size=10`);
    expect(req.request.method).toBe('GET');
    req.flush(lista, { status: 200, statusText: 'OK' });
  });

  it('S3: obtenerPorId() recibe 200 cuando existe y 404 cuando no existe', () => {
    const paciente: PacienteDTO = {
      id: 'p-1',
      nombre: 'Ana',
      apellidos: 'García',
      email: 'ana@example.com'
    };

    service.obtenerPorId('p-1').subscribe((res) => {
      expect(res).toEqual(paciente);
    });
    const req200 = httpMock.expectOne(`${baseUrl}/p-1`);
    expect(req200.request.method).toBe('GET');
    req200.flush(paciente, { status: 200, statusText: 'OK' });

    let statusError = 0;
    service.obtenerPorId('inexistente').subscribe({
      next: () => fail('Debería fallar con 404'),
      error: (err: HttpErrorResponse) => {
        statusError = err.status;
      }
    });
    const req404 = httpMock.expectOne(`${baseUrl}/inexistente`);
    req404.flush('Not found', { status: 404, statusText: 'Not Found' });
    expect(statusError).toBe(404);
  });

  it('S4: eliminar() envía DELETE /pacientes/{id} y maneja 204 y 404', () => {
    let completado = false;
    service.eliminar('p-1').subscribe(() => {
      completado = true;
    });
    const req204 = httpMock.expectOne(`${baseUrl}/p-1`);
    expect(req204.request.method).toBe('DELETE');
    req204.flush(null, { status: 204, statusText: 'No Content' });
    expect(completado).toBeTrue();

    let statusError = 0;
    service.eliminar('p-404').subscribe({
      next: () => fail('Debería fallar con 404'),
      error: (err: HttpErrorResponse) => {
        statusError = err.status;
      }
    });
    const req404 = httpMock.expectOne(`${baseUrl}/p-404`);
    req404.flush('Not found', { status: 404, statusText: 'Not Found' });
    expect(statusError).toBe(404);
  });
});
