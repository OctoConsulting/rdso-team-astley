import { Component, OnInit, Directive, Input, Output, EventEmitter, ViewChildren, QueryList } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISong } from '../song.model';
import { SongService } from '../service/song.service';
import { SongDeleteDialogComponent } from '../delete/song-delete-dialog.component';

import * as _ from 'lodash';

import dayjs from 'dayjs/esm';

export type SortColumn = keyof ISong | '';
export type SortDirection = 'asc' | 'desc' | '';
const rotate: { [key: string]: SortDirection } = { asc: 'desc', desc: '', '': 'asc' };

const compare = (v1: any, v2: any): number => {
  const a = dayjs.duration(v1).asMilliseconds() - dayjs.duration(v2).asMilliseconds();
  return a;
};

export interface SortEvent {
  column: SortColumn;
  direction: SortDirection;
}

@Directive({
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: 'th[sortable]',
  // eslint-disable-next-line @angular-eslint/no-host-metadata-property
  host: {
    '[class.asc]': 'direction === "asc"',
    '[class.desc]': 'direction === "desc"',
    '(click)': 'rotate()',
  },
})
// eslint-disable-next-line @angular-eslint/directive-class-suffix
export class NgbdSortableHeader {
  @Input() sortable: SortColumn = '';
  @Input() direction: SortDirection = '';
  @Output() sort = new EventEmitter<SortEvent>();

  rotate(): void {
    this.direction = rotate[this.direction];
    this.sort.emit({ column: this.sortable, direction: this.direction });
  }
}

@Component({
  selector: 'jhi-song',
  templateUrl: './song.component.html',
})
export class SongComponent implements OnInit {
  songs?: ISong[];
  standardSortSongs?: ISong[];
  isLoading = false;
  @ViewChildren(NgbdSortableHeader) headers!: QueryList<NgbdSortableHeader>;

  constructor(protected songService: SongService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.songService.query().subscribe({
      next: (res: HttpResponse<ISong[]>) => {
        this.isLoading = false;
        this.songs = res.body ?? [];
        this.standardSortSongs = _.cloneDeep(this.songs);
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
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

  onSort({ column, direction }: SortEvent): number | void {
    // resetting other headers
    this.headers.forEach(header => {
      if (header.sortable !== column) {
        header.direction = '';
      }
    });

    // sorting countries
    if (direction === '' || column === '') {
      this.songs = _.cloneDeep(this.standardSortSongs);
    } else {
      this.songs = [...this.songs!].sort((a, b) => {
        const res = compare(a[column], b[column]);
        return direction === 'asc' ? res : -res;
      });
    }
  }
}
