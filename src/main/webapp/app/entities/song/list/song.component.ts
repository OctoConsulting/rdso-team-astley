import { Component, OnInit, Directive, Input, Output, EventEmitter, ViewChildren, QueryList } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISong } from '../song.model';

import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/config/pagination.constants';
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
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page?: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;

  constructor(
    protected songService: SongService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal
  ) {}

  loadPage(page?: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    const pageToLoad: number = page ?? this.page ?? 1;

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
    // this.songService
    //   .query({
    //     page: pageToLoad - 1,
    //     size: this.itemsPerPage,
    //     sort: this.sort(),
    //   })
    //   .subscribe({
    //     next: (res: HttpResponse<ISong[]>) => {
    //       this.isLoading = false;
    //       this.onSuccess(res.body, res.headers, pageToLoad, !dontNavigate);
    //     },
    //     error: () => {
    //       this.isLoading = false;
    //       this.onError();
    //     },
    //   });
  }

  ngOnInit(): void {
    this.handleNavigation();
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
        this.loadPage();
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

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected handleNavigation(): void {
    combineLatest([this.activatedRoute.data, this.activatedRoute.queryParamMap]).subscribe(([data, params]) => {
      const page = params.get('page');
      const pageNumber = +(page ?? 1);
      const sort = (params.get(SORT) ?? data['defaultSort']).split(',');
      const predicate = sort[0];
      const ascending = sort[1] === ASC;
      if (pageNumber !== this.page || predicate !== this.predicate || ascending !== this.ascending) {
        this.predicate = predicate;
        this.ascending = ascending;
        this.loadPage(pageNumber, true);
      }
    });
  }

  // protected onSuccess(data: ISong[] | null, headers: HttpHeaders, page: number, navigate: boolean): void {
  //   this.totalItems = Number(headers.get('X-Total-Count'));
  //   this.page = page;
  //   if (navigate) {
  //     this.router.navigate(['/song'], {
  //       queryParams: {
  //         page: this.page,
  //         size: this.itemsPerPage,
  //         sort: this.predicate + ',' + (this.ascending ? ASC : DESC),
  //       },
  //     });
  //   }
  //   this.songs = data ?? [];
  //   this.ngbPaginationPage = this.page;
  // }

  // protected onError(): void {
  //   this.ngbPaginationPage = this.page ?? 1;
  // }
}
