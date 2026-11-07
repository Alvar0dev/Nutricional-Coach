import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

import { PacienteDTO } from '../../core/models/paciente.dto';
import { PacienteService } from '../../core/services/paciente.service';

/**
 * Página de detalle de paciente (S3).
 * Consume GET /api/v1/pacientes/{id}. Responde 200 con PacienteDTO o 404 si no existe.
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

  volver(): void {
    this.router.navigate(['/pacientes']);
  }
}
