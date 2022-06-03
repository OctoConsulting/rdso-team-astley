import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { INotes } from '../notes.model';
import { NotesService } from '../service/notes.service';
import { NotesDeleteDialogComponent } from '../delete/notes-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-notes',
  templateUrl: './notes.component.html',
})
export class NotesComponent implements OnInit {
  notes?: INotes[];
  isLoading = false;

  constructor(protected notesService: NotesService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.notesService.query().subscribe({
      next: (res: HttpResponse<INotes[]>) => {
        this.isLoading = false;
        this.notes = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: INotes): string {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(notes: INotes): void {
    const modalRef = this.modalService.open(NotesDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.notes = notes;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
