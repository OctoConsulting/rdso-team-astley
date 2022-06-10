import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISong } from '../song.model';
import { SongService } from '../service/song.service';
import { SongDeleteDialogComponent } from '../delete/song-delete-dialog.component';
import { faSearch } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'jhi-song',
  templateUrl: './song.component.html',
})
export class SongComponent implements OnInit {
  songs?: ISong[];
  isLoading = false;
  model = {performer: '', writer: ''};

  constructor(protected songService: SongService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.songService.query().subscribe({
      next: (res: HttpResponse<ISong[]>) => {
        this.isLoading = false;
        this.songs = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
    this.model.performer = '';
    this.model.writer = '';
  }

  trackId(_index: number, item: ISong): number {
    return item.id!;
  }

  delete(song: ISong): void {
    const modalRef = this.modalService.open(SongDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.song = song;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
