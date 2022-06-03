import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IWriter } from '../writer.model';

@Component({
  selector: 'jhi-writer-detail',
  templateUrl: './writer-detail.component.html',
})
export class WriterDetailComponent implements OnInit {
  writer: IWriter | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ writer }) => {
      this.writer = writer;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
