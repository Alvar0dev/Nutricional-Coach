import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

import { RevisionDTO } from '../../core/models/revision.dto';
import { RevisionService } from '../../core/services/revision.service';

/**
 * Página de listado de revisiones mensuales (S7, S9).
 * Consume GET /api/v1/revisiones y permite navegar al detalle o descargar el PDF de cada revisión.
 */
@Component({
  selector: 'app-revisiones-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './revisiones-list.component.html',
  styleUrl: './revisiones-list.component.css'
})
export class RevisionesListComponent implements OnInit {
  revisiones: RevisionDTO[] = [];
  cargando = false;
  error = '';
  descargandoId: string | null = null;
  errorPdf = '';

  constructor(
    private readonly revisionService: RevisionService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando = true;
    this.error = '';
    this.revisionService.listar().subscribe({
      next: (revisiones) => {
        this.revisiones = revisiones;
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar las revisiones.';
        this.cargando = false;
      }
    });
  }

  irNueva(): void {
    this.router.navigate(['/revisiones/nueva']);
  }

  verDetalle(id: string): void {
    this.router.navigate(['/revisiones', id]);
  }

  descargarPdf(id: string): void {
    this.descargandoId = id;
    this.errorPdf = '';
    this.revisionService.descargarPdf(id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `revision-${id}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
        this.descargandoId = null;
      },
      error: (err: HttpErrorResponse) => {
        this.errorPdf =
          err.status === 404 ? 'Revisión no encontrada para generar PDF.' : 'No se pudo descargar el PDF.';
        this.descargandoId = null;
      }
    });
  }
}
