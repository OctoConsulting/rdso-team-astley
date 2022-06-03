import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IWriter, Writer } from '../writer.model';
import { WriterService } from '../service/writer.service';

@Component({
  selector: 'jhi-writer-update',
  templateUrl: './writer-update.component.html',
})
export class WriterUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [],
    songId: [],
  });

  constructor(protected writerService: WriterService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ writer }) => {
      this.updateForm(writer);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const writer = this.createFromForm();
    if (writer.id !== undefined) {
      this.subscribeToSaveResponse(this.writerService.update(writer));
    } else {
      this.subscribeToSaveResponse(this.writerService.create(writer));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IWriter>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(writer: IWriter): void {
    this.editForm.patchValue({
      id: writer.id,
      name: writer.name,
      songId: writer.songId,
    });
  }

  protected createFromForm(): IWriter {
    return {
      ...new Writer(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      songId: this.editForm.get(['songId'])!.value,
    };
  }
}
