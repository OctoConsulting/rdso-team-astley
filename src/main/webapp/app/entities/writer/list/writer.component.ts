import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IWriter } from '../writer.model';
import { WriterService } from '../service/writer.service';
import { WriterDeleteDialogComponent } from '../delete/writer-delete-dialog.component';

@Component({
  selector: 'jhi-writer',
  templateUrl: './writer.component.html',
})
export class WriterComponent implements OnInit {
  writers?: IWriter[];
  isLoading = false;

  constructor(protected writerService: WriterService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.writerService.query().subscribe({
      next: (res: HttpResponse<IWriter[]>) => {
        this.isLoading = false;
        this.writers = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IWriter): string {
    return item.id!;
  }

  delete(writer: IWriter): void {
    const modalRef = this.modalService.open(WriterDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.writer = writer;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
