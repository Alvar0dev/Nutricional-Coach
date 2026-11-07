import { Routes } from '@angular/router';

import { PacientesListComponent } from './pages/pacientes-list/pacientes-list.component';
import { PacientesNuevoComponent } from './pages/pacientes-nuevo/pacientes-nuevo.component';
import { PacientesDetalleComponent } from './pages/pacientes-detalle/pacientes-detalle.component';

export const routes: Routes = [
  { path: '', redirectTo: '/pacientes', pathMatch: 'full' },
  { path: 'pacientes', component: PacientesListComponent },
  { path: 'pacientes/nuevo', component: PacientesNuevoComponent },
  { path: 'pacientes/:id', component: PacientesDetalleComponent }
];
