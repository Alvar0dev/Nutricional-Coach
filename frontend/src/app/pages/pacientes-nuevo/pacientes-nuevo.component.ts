import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { PacienteDTO } from '../../core/models/paciente.dto';
import { PacienteService } from '../../core/services/paciente.service';

/**
 * Página de alta de paciente (S1).
 * Consume POST /api/v1/pacientes. Responde 201 Created + Location o 400 si falta nombre/email.
 */
@Component({
  selector: 'app-pacientes-nuevo',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pacientes-nuevo.component.html',
  styleUrl: './pacientes-nuevo.component.css'
})
export class PacientesNuevoComponent {
  paciente: Omit<PacienteDTO, 'id' | 'fechaAlta'> = {
    nombre: '',
    apellidos: '',
    email: '',
    telefono: '',
    objetivo: ''
  };
  cargando = false;
  error = '';
  exito = '';
  pacienteCreado?: PacienteDTO;

  constructor(
    private readonly pacienteService: PacienteService,
    private readonly router: Router
  ) {}

  guardar(): void {
    this.cargando = true;
    this.error = '';
    this.exito = '';
    this.pacienteCreado = undefined;

    this.pacienteService.crear(this.paciente).subscribe({
      next: (creado) => {
        this.pacienteCreado = creado;
        this.cargando = false;
        this.exito = `Paciente dado de alta (ID: ${creado.id ?? '-'})`;
      },
      error: () => {
        this.error = 'No se pudo dar de alta el paciente. Verifica nombre y email.';
        this.cargando = false;
      }
    });
  }

  irListado(): void {
    this.router.navigate(['/pacientes']);
  }
}
