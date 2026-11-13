import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

import { MedicionBiometricaDTO } from '../../core/models/medicion.dto';
import { MedicionService } from '../../core/services/medicion.service';

/**
 * Página de listado de mediciones biométricas de un paciente (S6).
 * Consume GET /api/v1/pacientes/{id}/mediciones y muestra el historial de mediciones.
 */
@Component({
  selector: 'app-mediciones-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mediciones-list.component.html',
  styleUrl: './mediciones-list.component.css'
})
export class MedicionesListComponent implements OnInit {
  pacienteId = '';
  mediciones: MedicionBiometricaDTO[] = [];
  cargando = false;
  error = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly medicionService: MedicionService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.error = 'ID de paciente no válido.';
      return;
    }
    this.pacienteId = id;
    this.cargar(id);
  }

  cargar(pacienteId: string): void {
    this.cargando = true;
    this.error = '';
    this.medicionService.listarPorPaciente(pacienteId).subscribe({
      next: (mediciones) => {
        this.mediciones = mediciones;
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar las mediciones del paciente.';
        this.cargando = false;
      }
    });
  }

  irNuevo(): void {
    this.router.navigate(['/pacientes', this.pacienteId, 'mediciones', 'nueva']);
  }

  volver(): void {
    this.router.navigate(['/pacientes']);
  }
}
