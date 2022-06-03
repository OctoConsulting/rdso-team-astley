import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { NotesComponent } from './list/notes.component';
import { NotesDetailComponent } from './detail/notes-detail.component';
import { NotesUpdateComponent } from './update/notes-update.component';
import { NotesDeleteDialogComponent } from './delete/notes-delete-dialog.component';
import { NotesRoutingModule } from './route/notes-routing.module';

@NgModule({
  imports: [SharedModule, NotesRoutingModule],
  declarations: [NotesComponent, NotesDetailComponent, NotesUpdateComponent, NotesDeleteDialogComponent],
  entryComponents: [NotesDeleteDialogComponent],
})
export class NotesModule {}
