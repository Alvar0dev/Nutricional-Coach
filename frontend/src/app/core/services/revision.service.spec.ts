import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { API_BASE_URL } from '../api.config';
import { RevisionDTO } from '../models/revision.dto';
import { RevisionService } from './revision.service';

describe('RevisionService (S7, S8, S9)', () => {
  let service: RevisionService;
  let httpMock: HttpTestingController;
  const baseUrl = `${API_BASE_URL}/revisiones`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), RevisionService]
    });
    service = TestBed.inject(RevisionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('S7: listar() y obtenerPorId() manejan 200 y 404', () => {
    const rev: RevisionDTO = {
      id: 'rev-1',
      pacienteId: 'p-1',
      deltaPeso: -1.2,
      deltaGrasa: -0.5,
      deltaMusculo: 0.4
    };

    service.listar().subscribe((res) => {
      expect(res).toEqual([rev]);
    });
    const reqList = httpMock.expectOne(baseUrl);
    expect(reqList.request.method).toBe('GET');
    reqList.flush([rev], { status: 200, statusText: 'OK' });

    service.obtenerPorId('rev-1').subscribe((res) => {
      expect(res.id).toBe('rev-1');
    });
    const reqGet = httpMock.expectOne(`${baseUrl}/rev-1`);
    expect(reqGet.request.method).toBe('GET');
    reqGet.flush(rev, { status: 200, statusText: 'OK' });

    let statusError = 0;
    service.obtenerPorId('rev-404').subscribe({
      next: () => fail('Debería fallar con 404'),
      error: (err: HttpErrorResponse) => {
        statusError = err.status;
      }
    });
    const req404 = httpMock.expectOne(`${baseUrl}/rev-404`);
    req404.flush('Not found', { status: 404, statusText: 'Not Found' });
    expect(statusError).toBe(404);
  });

  it('S8: crear() envía POST /pacientes/{id}/revisiones y recibe 201', () => {
    const entrada: Omit<RevisionDTO, 'id'> = {
      pacienteId: 'p-1',
      deltaPeso: -1.0,
      deltaGrasa: -0.4,
      deltaMusculo: 0.3,
      ajustesDieta: 'Subir proteína'
    };
    const creada: RevisionDTO = { ...entrada, id: 'rev-10' };

    service.crear('p-1', entrada).subscribe((res) => {
      expect(res).toEqual(creada);
    });

    const req = httpMock.expectOne(`${API_BASE_URL}/pacientes/p-1/revisiones`);
    expect(req.request.method).toBe('POST');
    req.flush(creada, { status: 201, statusText: 'Created' });
  });

  it('S9: descargarPdf() envía GET /revisiones/{id}/pdf con blob y maneja 200 y 404', () => {
    const pdfBlob = new Blob(['%PDF-1.4'], { type: 'application/pdf' });

    service.descargarPdf('rev-1').subscribe((res) => {
      expect(res.size).toBeGreaterThan(0);
    });
    const req200 = httpMock.expectOne(`${baseUrl}/rev-1/pdf`);
    expect(req200.request.method).toBe('GET');
    expect(req200.request.responseType).toBe('blob');
    req200.flush(pdfBlob, { status: 200, statusText: 'OK' });

    let statusError = 0;
    service.descargarPdf('rev-404').subscribe({
      next: () => fail('Debería fallar con 404'),
      error: (err: HttpErrorResponse) => {
        statusError = err.status;
      }
    });
    const req404 = httpMock.expectOne(`${baseUrl}/rev-404/pdf`);
    req404.flush(new Blob(), { status: 404, statusText: 'Not Found' });
    expect(statusError).toBe(404);
  });
});
