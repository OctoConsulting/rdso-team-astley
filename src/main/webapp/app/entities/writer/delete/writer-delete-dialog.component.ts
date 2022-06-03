import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IWriter } from '../writer.model';
import { WriterService } from '../service/writer.service';

@Component({
  templateUrl: './writer-delete-dialog.component.html',
})
export class WriterDeleteDialogComponent {
  writer?: IWriter;

  constructor(protected writerService: WriterService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.writerService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
