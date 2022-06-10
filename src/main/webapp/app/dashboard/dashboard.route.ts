import { Route } from '@angular/router';

import { DashboardComponent } from './dashboard.component';
import { Authority } from '../config/authority.constants';
import { UserRouteAccessService } from '../core/auth/user-route-access.service';

export const DASHBOARD_ROUTE: Route = {
  path: 'dashboard',
  component: DashboardComponent,
  data: {
    authorities: [Authority.ADMIN],
    pageTitle: 'Dashboard',
  },
  canActivate: [UserRouteAccessService],
};
