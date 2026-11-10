import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';

import { PacienteDTO } from '../../core/models/paciente.dto';
import { PacienteService } from '../../core/services/paciente.service';

/**
 * Página de detalle de paciente (S3, S4).
 * Consume GET /api/v1/pacientes/{id} (200/404) y permite dar de baja con DELETE /api/v1/pacientes/{id} (204/404).
 */
@Component({
  selector: 'app-pacientes-detalle',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pacientes-detalle.component.html',
  styleUrl: './pacientes-detalle.component.css'
})
export class PacientesDetalleComponent implements OnInit {
  paciente?: PacienteDTO;
  cargando = false;
  error = '';
  eliminando = false;
  errorEliminar = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly pacienteService: PacienteService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.error = 'ID de paciente no válido.';
      return;
    }
    this.cargar(id);
  }

  cargar(id: string): void {
    this.cargando = true;
    this.error = '';
    this.pacienteService.obtenerPorId(id).subscribe({
      next: (paciente) => {
        this.paciente = paciente;
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se encontró el paciente.';
        this.cargando = false;
      }
    });
  }

  irMediciones(): void {
    if (this.paciente?.id) {
      this.router.navigate(['/pacientes', this.paciente.id, 'mediciones']);
    }
  }

  eliminar(): void {
    if (!this.paciente?.id) {
      return;
    }
    if (!confirm(`¿Estás seguro de dar de baja al paciente ${this.paciente.nombre} ${this.paciente.apellidos}?`)) {
      return;
    }
    this.eliminando = true;
    this.errorEliminar = '';
    this.pacienteService.eliminar(this.paciente.id).subscribe({
      next: () => {
        this.eliminando = false;
        this.router.navigate(['/pacientes']);
      },
      error: (err: HttpErrorResponse) => {
        this.errorEliminar =
          err.status === 404 ? 'Paciente no encontrado.' : 'No se pudo eliminar el paciente.';
        this.eliminando = false;
      }
    });
  }

  volver(): void {
    this.router.navigate(['/pacientes']);
  }
}
