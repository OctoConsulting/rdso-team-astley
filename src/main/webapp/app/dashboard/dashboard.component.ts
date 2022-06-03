import { Component, Directive, EventEmitter, Input, OnInit, Output, PipeTransform, QueryList, ViewChildren } from '@angular/core';
import { FormControl } from '@angular/forms';
import { AccountService } from 'app/core/auth/account.service';
import { Observable } from 'rxjs';

interface Country {
  id: number;
  name: string;
  flag: string;
  area: number;
  population: number;
}

interface Song {
  number: number;
  title: string;
  writer: string;
  performer: string;
  length: string;
}

const SONGS: Song[] = [
  {
    number: 1,
    title: 'The Power of Love',
    writer: 'Huey Lewis; Chris Hayes; Johnny Colla',
    performer: 'Huey Lewis and the News',
    length: '3:58',
  },
  {
    number: 2,
    title: 'The Power of Love',
    writer: 'Huey Lewis; Chris Hayes',
    performer: 'Huey Lewis and the News',
    length: '3:59',
  },
  {
    number: 3,
    title: 'The Power of Love',
    writer: 'Huey Lewis; Chris Hayes',
    performer: 'Huey Lewis and the News',
    length: '3:57',
  },
];

const COUNTRIES: Country[] = [
  {
    id: 1,
    name: 'Russia',
    flag: 'f/f3/Flag_of_Russia.svg',
    area: 17075200,
    population: 146989754,
  },
  {
    id: 2,
    name: 'Canada',
    flag: 'c/cf/Flag_of_Canada.svg',
    area: 9976140,
    population: 36624199,
  },
  {
    id: 3,
    name: 'United States',
    flag: 'a/a4/Flag_of_the_United_States.svg',
    area: 9629091,
    population: 324459463,
  },
  {
    id: 4,
    name: 'China',
    flag: 'f/fa/Flag_of_the_People%27s_Republic_of_China.svg',
    area: 9596960,
    population: 1409517397,
  },
];
export type SortColumn = keyof Song | '';
export type SortDirection = 'asc' | 'desc' | '';
const rotate: { [key: string]: SortDirection } = { asc: 'desc', desc: '', '': 'asc' };

const compare = (v1: string | number, v2: string | number): number => (v1 < v2 ? -1 : v1 > v2 ? 1 : 0);

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
  selector: 'jhi-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent {
  songs = SONGS;
  songs$!: Observable<Song[]>;
  @ViewChildren(NgbdSortableHeader) headers!: QueryList<NgbdSortableHeader>;
  filter = new FormControl('');
  constructor(private accountService: AccountService) {}

  onSort({ column, direction }: SortEvent): number | void {
    // resetting other headers
    this.headers.forEach(header => {
      if (header.sortable !== column) {
        header.direction = '';
      }
    });

    // sorting countries
    if (direction === '' || column === '') {
      this.songs = SONGS;
    } else {
      this.songs = [...SONGS].sort((a, b) => {
        const res = compare(a[column], b[column]);
        return direction === 'asc' ? res : -res;
      });
    }
  }
}
