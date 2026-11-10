import { Routes } from '@angular/router';

import { PacientesListComponent } from './pages/pacientes-list/pacientes-list.component';
import { PacientesNuevoComponent } from './pages/pacientes-nuevo/pacientes-nuevo.component';
import { PacientesDetalleComponent } from './pages/pacientes-detalle/pacientes-detalle.component';
import { MedicionesListComponent } from './pages/mediciones-list/mediciones-list.component';
import { MedicionNuevoComponent } from './pages/medicion-nuevo/medicion-nuevo.component';
import { RevisionesListComponent } from './pages/revisiones-list/revisiones-list.component';
import { RevisionesNuevoComponent } from './pages/revisiones-nuevo/revisiones-nuevo.component';
import { RevisionesDetalleComponent } from './pages/revisiones-detalle/revisiones-detalle.component';

export const routes: Routes = [
  { path: '', redirectTo: '/pacientes', pathMatch: 'full' },
  { path: 'pacientes', component: PacientesListComponent },
  { path: 'pacientes/nuevo', component: PacientesNuevoComponent },
  { path: 'pacientes/:id', component: PacientesDetalleComponent },
  { path: 'pacientes/:id/mediciones', component: MedicionesListComponent },
  { path: 'pacientes/:id/mediciones/nueva', component: MedicionNuevoComponent },
  { path: 'pacientes/:id/revisiones/nueva', component: RevisionesNuevoComponent },
  { path: 'revisiones', component: RevisionesListComponent },
  { path: 'revisiones/nueva', component: RevisionesNuevoComponent },
  { path: 'revisiones/:id', component: RevisionesDetalleComponent }
];
