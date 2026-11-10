import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';

import { RevisionDTO } from '../../core/models/revision.dto';
import { RevisionService } from '../../core/services/revision.service';

/**
 * Página de detalle de revisión mensual y descarga de PDF (S7, S9).
 * Consume GET /api/v1/revisiones/{id} (200/404) y GET /api/v1/revisiones/{id}/pdf (application/pdf).
 */
@Component({
  selector: 'app-revisiones-detalle',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './revisiones-detalle.component.html',
  styleUrl: './revisiones-detalle.component.css'
})
export class RevisionesDetalleComponent implements OnInit {
  revision?: RevisionDTO;
  cargando = false;
  error = '';
  descargandoPdf = false;
  errorPdf = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly revisionService: RevisionService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.error = 'ID de revisión no válido.';
      return;
    }
    this.cargar(id);
  }

  cargar(id: string): void {
    this.cargando = true;
    this.error = '';
    this.revisionService.obtenerPorId(id).subscribe({
      next: (revision) => {
        this.revision = revision;
        this.cargando = false;
      },
      error: (err: HttpErrorResponse) => {
        this.error = err.status === 404 ? 'No se encontró la revisión.' : 'Error al cargar la revisión.';
        this.cargando = false;
      }
    });
  }

  descargarPdf(): void {
    if (!this.revision?.id) {
      return;
    }
    const id = this.revision.id;
    this.descargandoPdf = true;
    this.errorPdf = '';
    this.revisionService.descargarPdf(id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `revision-${id}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
        this.descargandoPdf = false;
      },
      error: (err: HttpErrorResponse) => {
        this.errorPdf =
          err.status === 404 ? 'No se encontró el PDF de la revisión.' : 'No se pudo descargar el PDF.';
        this.descargandoPdf = false;
      }
    });
  }

  volver(): void {
    this.router.navigate(['/revisiones']);
  }
}
