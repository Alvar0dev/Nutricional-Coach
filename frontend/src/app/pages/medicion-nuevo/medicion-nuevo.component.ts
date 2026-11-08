import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { MedicionBiometricaDTO } from '../../core/models/medicion.dto';
import { MedicionService } from '../../core/services/medicion.service';

/**
 * Página de registro de medición biométrica (S5).
 * Consume POST /api/v1/pacientes/{id}/mediciones. Responde 201 Created + Location o 400 si faltan datos.
 */
@Component({
  selector: 'app-medicion-nuevo',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './medicion-nuevo.component.html',
  styleUrl: './medicion-nuevo.component.css'
})
export class MedicionNuevoComponent {
  pacienteId = '';
  medicion: Omit<MedicionBiometricaDTO, 'id' | 'pacienteId'> = {
    fecha: '',
    pesoKg: 0,
    grasaPct: 0,
    masaMuscularKg: undefined,
    cinturaCm: undefined
  };
  cargando = false;
  error = '';
  exito = '';
  medicionCreada?: MedicionBiometricaDTO;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly medicionService: MedicionService
  ) {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.pacienteId = id;
    }
  }

  guardar(): void {
    this.cargando = true;
    this.error = '';
    this.exito = '';
    this.medicionCreada = undefined;

    this.medicionService.registrar(this.pacienteId, this.medicion).subscribe({
      next: (creada) => {
        this.medicionCreada = creada;
        this.cargando = false;
        this.exito = 'Medición registrada correctamente.';
      },
      error: () => {
        this.error = 'No se pudo registrar la medición. Verifica peso y grasa corporal.';
        this.cargando = false;
      }
    });
  }

  volver(): void {
    this.router.navigate(['/pacientes', this.pacienteId, 'mediciones']);
  }
}
