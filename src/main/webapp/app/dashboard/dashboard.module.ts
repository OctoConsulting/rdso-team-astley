import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { DASHBOARD_ROUTE } from './dashboard.route';
import { DashboardComponent, NgbdSortableHeader } from './dashboard.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([DASHBOARD_ROUTE]), FormsModule, ReactiveFormsModule],
  declarations: [DashboardComponent],
})
export class DashboardModule {}
