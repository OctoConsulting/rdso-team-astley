import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { NotesComponent } from '../list/notes.component';
import { NotesDetailComponent } from '../detail/notes-detail.component';
import { NotesUpdateComponent } from '../update/notes-update.component';
import { NotesRoutingResolveService } from './notes-routing-resolve.service';

const notesRoute: Routes = [
  {
    path: '',
    component: NotesComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: NotesDetailComponent,
    resolve: {
      notes: NotesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NotesUpdateComponent,
    resolve: {
      notes: NotesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: NotesUpdateComponent,
    resolve: {
      notes: NotesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(notesRoute)],
  exports: [RouterModule],
})
export class NotesRoutingModule {}
