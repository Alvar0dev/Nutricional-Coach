import { Routes } from '@angular/router';

import { PacientesListComponent } from './pages/pacientes-list/pacientes-list.component';
import { PacientesNuevoComponent } from './pages/pacientes-nuevo/pacientes-nuevo.component';
import { PacientesDetalleComponent } from './pages/pacientes-detalle/pacientes-detalle.component';
import { MedicionesListComponent } from './pages/mediciones-list/mediciones-list.component';
import { MedicionNuevoComponent } from './pages/medicion-nuevo/medicion-nuevo.component';

export const routes: Routes = [
  { path: '', redirectTo: '/pacientes', pathMatch: 'full' },
  { path: 'pacientes', component: PacientesListComponent },
  { path: 'pacientes/nuevo', component: PacientesNuevoComponent },
  { path: 'pacientes/:id', component: PacientesDetalleComponent },
  { path: 'pacientes/:id/mediciones', component: MedicionesListComponent },
  { path: 'pacientes/:id/mediciones/nueva', component: MedicionNuevoComponent }
];
