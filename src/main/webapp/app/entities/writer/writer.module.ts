import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { WriterComponent } from './list/writer.component';
import { WriterDetailComponent } from './detail/writer-detail.component';
import { WriterUpdateComponent } from './update/writer-update.component';
import { WriterDeleteDialogComponent } from './delete/writer-delete-dialog.component';
import { WriterRoutingModule } from './route/writer-routing.module';

@NgModule({
  imports: [SharedModule, WriterRoutingModule],
  declarations: [WriterComponent, WriterDetailComponent, WriterUpdateComponent, WriterDeleteDialogComponent],
  entryComponents: [WriterDeleteDialogComponent],
})
export class WriterModule {}
