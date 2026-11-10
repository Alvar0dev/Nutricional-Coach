import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { RecordatorioDTO } from '../../core/models/recordatorio.dto';
import { RecordatorioService } from '../../core/services/recordatorio.service';

/**
 * Página de listado de recordatorios pendientes y actualización de feedback (S10, S11).
 * Consume GET /api/v1/recordatorios y PATCH /api/v1/recordatorios/{id}.
 */
@Component({
  selector: 'app-recordatorios-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './recordatorios-list.component.html',
  styleUrl: './recordatorios-list.component.css'
})
export class RecordatoriosListComponent implements OnInit {
  recordatorios: RecordatorioDTO[] = [];
  feedbackPorId: Record<string, string> = {};
  cargando = false;
  error = '';
  guardandoId: string | null = null;
  errorFeedback = '';
  exitoFeedback = '';

  constructor(private readonly recordatorioService: RecordatorioService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando = true;
    this.error = '';
    this.recordatorioService.listarPendientes().subscribe({
      next: (recordatorios) => {
        this.recordatorios = recordatorios;
        this.feedbackPorId = {};
        for (const r of recordatorios) {
          if (r.id) {
            this.feedbackPorId[r.id] = r.feedbackPaciente ?? '';
          }
        }
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar los recordatorios pendientes.';
        this.cargando = false;
      }
    });
  }

  guardarFeedback(recordatorio: RecordatorioDTO): void {
    if (!recordatorio.id) {
      return;
    }
    const id = recordatorio.id;
    const texto = this.feedbackPorId[id] ?? '';
    this.guardandoId = id;
    this.errorFeedback = '';
    this.exitoFeedback = '';

    this.recordatorioService.actualizarFeedback(id, texto).subscribe({
      next: (actualizado) => {
        this.recordatorios = this.recordatorios.map((r) =>
          r.id === id ? actualizado : r
        );
        this.feedbackPorId[id] = actualizado.feedbackPaciente ?? texto;
        this.guardandoId = null;
        this.exitoFeedback = 'Feedback actualizado correctamente.';
      },
      error: (err: HttpErrorResponse) => {
        this.errorFeedback =
          err.status === 404
            ? 'Recordatorio no encontrado.'
            : 'No se pudo actualizar el feedback.';
        this.guardandoId = null;
      }
    });
  }
}
