import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISong } from '../song.model';
import { SongService } from '../service/song.service';

@Component({
  templateUrl: './song-delete-dialog.component.html',
})
export class SongDeleteDialogComponent {
  song?: ISong;

  constructor(protected songService: SongService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.songService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
