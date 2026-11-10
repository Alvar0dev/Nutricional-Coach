import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

import { PacienteDTO } from '../../core/models/paciente.dto';
import { PacienteService } from '../../core/services/paciente.service';

/**
 * Página de listado de pacientes (S2, S4).
 * Consume GET /api/v1/pacientes (muestra solo activos) y DELETE /api/v1/pacientes/{id} (soft-delete).
 */
@Component({
  selector: 'app-pacientes-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pacientes-list.component.html',
  styleUrl: './pacientes-list.component.css'
})
export class PacientesListComponent implements OnInit {
  pacientes: PacienteDTO[] = [];
  cargando = false;
  error = '';
  eliminandoId: string | null = null;
  errorEliminar: string | null = null;

  constructor(
    private readonly pacienteService: PacienteService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando = true;
    this.error = '';
    this.pacienteService.listar().subscribe({
      next: (pacientes) => {
        this.pacientes = pacientes;
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar los pacientes.';
        this.cargando = false;
      }
    });
  }

  irNuevo(): void {
    this.router.navigate(['/pacientes/nuevo']);
  }

  verDetalle(id: string): void {
    this.router.navigate(['/pacientes', id]);
  }

  eliminar(paciente: PacienteDTO): void {
    if (!paciente.id) {
      return;
    }
    if (!confirm(`¿Estás seguro de dar de baja al paciente ${paciente.nombre} ${paciente.apellidos}?`)) {
      return;
    }
    this.eliminandoId = paciente.id;
    this.errorEliminar = null;
    this.pacienteService.eliminar(paciente.id).subscribe({
      next: () => {
        this.pacientes = this.pacientes.filter((p) => p.id !== paciente.id);
        this.eliminandoId = null;
      },
      error: (err: HttpErrorResponse) => {
        this.errorEliminar =
          err.status === 404 ? 'Paciente no encontrado.' : 'No se pudo eliminar el paciente.';
        this.eliminandoId = null;
      }
    });
  }
}