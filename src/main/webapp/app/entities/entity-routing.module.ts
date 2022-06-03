import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'song',
        data: { pageTitle: 'Songs' },
        loadChildren: () => import('./song/song.module').then(m => m.SongModule),
      },
      {
        path: 'writer',
        data: { pageTitle: 'Writers' },
        loadChildren: () => import('./writer/writer.module').then(m => m.WriterModule),
      },
      {
        path: 'notes',
        data: { pageTitle: 'Notes' },
        loadChildren: () => import('./notes/notes.module').then(m => m.NotesModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
