import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { WriterComponent } from '../list/writer.component';
import { WriterDetailComponent } from '../detail/writer-detail.component';
import { WriterUpdateComponent } from '../update/writer-update.component';
import { WriterRoutingResolveService } from './writer-routing-resolve.service';

const writerRoute: Routes = [
  {
    path: '',
    component: WriterComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: WriterDetailComponent,
    resolve: {
      writer: WriterRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: WriterUpdateComponent,
    resolve: {
      writer: WriterRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: WriterUpdateComponent,
    resolve: {
      writer: WriterRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(writerRoute)],
  exports: [RouterModule],
})
export class WriterRoutingModule {}
