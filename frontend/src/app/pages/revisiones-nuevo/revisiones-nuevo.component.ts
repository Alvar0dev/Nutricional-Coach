import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { MedicionBiometricaDTO } from '../../core/models/medicion.dto';
import { RevisionDTO } from '../../core/models/revision.dto';
import { MedicionService } from '../../core/services/medicion.service';
import { RevisionService } from '../../core/services/revision.service';

/**
 * Página de creación de revisión mensual con cálculo de deltas (S8).
 * Consume POST /api/v1/pacientes/{id}/revisiones. Responde 201 Created + Location.
 */
@Component({
  selector: 'app-revisiones-nuevo',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './revisiones-nuevo.component.html',
  styleUrl: './revisiones-nuevo.component.css'
})
export class RevisionesNuevoComponent implements OnInit {
  revision: Omit<RevisionDTO, 'id'> = {
    pacienteId: '',
    medicionAnteriorId: '',
    medicionActualId: '',
    deltaPeso: 0,
    deltaGrasa: 0,
    deltaMusculo: 0,
    ajustesDieta: '',
    ajustesEntreno: '',
    comentariosAjuste: '',
    fechaRevision: ''
  };
  mediciones: MedicionBiometricaDTO[] = [];
  cargando = false;
  error = '';
  exito = '';
  revisionCreada?: RevisionDTO;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly revisionService: RevisionService,
    private readonly medicionService: MedicionService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.revision.pacienteId = id;
      this.cargarMediciones(id);
    }
  }

  cargarMediciones(pacienteId: string): void {
    if (!pacienteId.trim()) {
      this.mediciones = [];
      return;
    }
    this.medicionService.listarPorPaciente(pacienteId.trim()).subscribe({
      next: (mediciones) => {
        this.mediciones = mediciones;
      },
      error: () => {
        this.mediciones = [];
      }
    });
  }

  recalcularDeltas(): void {
    const anterior = this.mediciones.find((m) => m.id === this.revision.medicionAnteriorId);
    const actual = this.mediciones.find((m) => m.id === this.revision.medicionActualId);
    if (anterior && actual) {
      this.revision.deltaPeso = Number((actual.pesoKg - anterior.pesoKg).toFixed(2));
      this.revision.deltaGrasa = Number((actual.grasaPct - anterior.grasaPct).toFixed(2));
      const muscAnt = anterior.masaMuscularKg ?? 0;
      const muscAct = actual.masaMuscularKg ?? 0;
      this.revision.deltaMusculo = Number((muscAct - muscAnt).toFixed(2));
    }
  }

  guardar(): void {
    if (!this.revision.pacienteId.trim()) {
      this.error = 'El ID del paciente es obligatorio.';
      return;
    }
    this.cargando = true;
    this.error = '';
    this.exito = '';
    this.revisionCreada = undefined;

    const payload: Omit<RevisionDTO, 'id'> = {
      ...this.revision,
      pacienteId: this.revision.pacienteId.trim(),
      fechaRevision: this.revision.fechaRevision || undefined
    };

    this.revisionService.crear(payload.pacienteId, payload).subscribe({
      next: (creada) => {
        this.revisionCreada = creada;
        this.cargando = false;
        this.exito = 'Revisión mensual creada correctamente.';
      },
      error: () => {
        this.error = 'No se pudo crear la revisión mensual.';
        this.cargando = false;
      }
    });
  }

  verDetalleCreada(): void {
    if (this.revisionCreada?.id) {
      this.router.navigate(['/revisiones', this.revisionCreada.id]);
    }
  }

  volver(): void {
    this.router.navigate(['/revisiones']);
  }
}
