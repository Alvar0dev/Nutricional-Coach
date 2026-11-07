import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { PacienteDTO } from '../../core/models/paciente.dto';
import { PacienteService } from '../../core/services/paciente.service';

/**
 * Página de listado de pacientes (S2).
 * Consume GET /api/v1/pacientes y muestra solo los pacientes activos.
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
}
