import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { INotes, Notes } from '../notes.model';
import { NotesService } from '../service/notes.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ISong } from 'app/entities/song/song.model';
import { SongService } from 'app/entities/song/service/song.service';

@Component({
  selector: 'jhi-notes-update',
  templateUrl: './notes-update.component.html',
})
export class NotesUpdateComponent implements OnInit {
  isSaving = false;

  songsCollection: ISong[] = [];

  editForm = this.fb.group({
    id: [],
    userId: [],
    note: [],
    song: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected notesService: NotesService,
    protected songService: SongService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notes }) => {
      this.updateForm(notes);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('rmssApp.error', { message: err.message })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const notes = this.createFromForm();
    if (notes.id !== undefined) {
      this.subscribeToSaveResponse(this.notesService.update(notes));
    } else {
      this.subscribeToSaveResponse(this.notesService.create(notes));
    }
  }

  trackSongById(_index: number, item: ISong): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INotes>>): void {
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

  protected updateForm(notes: INotes): void {
    this.editForm.patchValue({
      id: notes.id,
      userId: notes.userId,
      note: notes.note,
      song: notes.song,
    });

    this.songsCollection = this.songService.addSongToCollectionIfMissing(this.songsCollection, notes.song);
  }

  protected loadRelationshipsOptions(): void {
    this.songService
      .query({ filter: 'note-is-null' })
      .pipe(map((res: HttpResponse<ISong[]>) => res.body ?? []))
      .pipe(map((songs: ISong[]) => this.songService.addSongToCollectionIfMissing(songs, this.editForm.get('song')!.value)))
      .subscribe((songs: ISong[]) => (this.songsCollection = songs));
  }

  protected createFromForm(): INotes {
    return {
      ...new Notes(),
      id: this.editForm.get(['id'])!.value,
      userId: this.editForm.get(['userId'])!.value,
      note: this.editForm.get(['note'])!.value,
      song: this.editForm.get(['song'])!.value,
    };
  }
}
