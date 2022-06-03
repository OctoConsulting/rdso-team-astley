import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ISong, Song } from '../song.model';
import { SongService } from '../service/song.service';

@Component({
  selector: 'jhi-song-update',
  templateUrl: './song-update.component.html',
})
export class SongUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    title: [],
    writer: [],
    performer: [],
    length: [],
    soundtrack: [],
    trackNumber: [],
    url: [],
  });

  constructor(protected songService: SongService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ song }) => {
      this.updateForm(song);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const song = this.createFromForm();
    if (song.id !== undefined) {
      this.subscribeToSaveResponse(this.songService.update(song));
    } else {
      this.subscribeToSaveResponse(this.songService.create(song));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISong>>): void {
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

  protected updateForm(song: ISong): void {
    this.editForm.patchValue({
      id: song.id,
      title: song.title,
      writer: song.writer,
      performer: song.performer,
      length: song.length,
      soundtrack: song.soundtrack,
      trackNumber: song.trackNumber,
      url: song.url,
    });
  }

  protected createFromForm(): ISong {
    return {
      ...new Song(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      writer: this.editForm.get(['writer'])!.value,
      performer: this.editForm.get(['performer'])!.value,
      length: this.editForm.get(['length'])!.value,
      soundtrack: this.editForm.get(['soundtrack'])!.value,
      trackNumber: this.editForm.get(['trackNumber'])!.value,
      url: this.editForm.get(['url'])!.value,
    };
  }
}
