import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { NotesService } from '../service/notes.service';
import { INotes, Notes } from '../notes.model';
import { ISong } from 'app/entities/song/song.model';
import { SongService } from 'app/entities/song/service/song.service';

import { NotesUpdateComponent } from './notes-update.component';

describe('Notes Management Update Component', () => {
  let comp: NotesUpdateComponent;
  let fixture: ComponentFixture<NotesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let notesService: NotesService;
  let songService: SongService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [NotesUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(NotesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NotesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    notesService = TestBed.inject(NotesService);
    songService = TestBed.inject(SongService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call song query and add missing value', () => {
      const notes: INotes = { id: 456 };
      const song: ISong = { id: 37436 };
      notes.song = song;

      const songCollection: ISong[] = [{ id: 27282 }];
      jest.spyOn(songService, 'query').mockReturnValue(of(new HttpResponse({ body: songCollection })));
      const expectedCollection: ISong[] = [song, ...songCollection];
      jest.spyOn(songService, 'addSongToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ notes });
      comp.ngOnInit();

      expect(songService.query).toHaveBeenCalled();
      expect(songService.addSongToCollectionIfMissing).toHaveBeenCalledWith(songCollection, song);
      expect(comp.songsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const notes: INotes = { id: 456 };
      const song: ISong = { id: 10871 };
      notes.song = song;

      activatedRoute.data = of({ notes });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(notes));
      expect(comp.songsCollection).toContain(song);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Notes>>();
      const notes = { id: 123 };
      jest.spyOn(notesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notes });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notes }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(notesService.update).toHaveBeenCalledWith(notes);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Notes>>();
      const notes = new Notes();
      jest.spyOn(notesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notes });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notes }));
      saveSubject.complete();

      // THEN
      expect(notesService.create).toHaveBeenCalledWith(notes);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Notes>>();
      const notes = { id: 123 };
      jest.spyOn(notesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notes });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(notesService.update).toHaveBeenCalledWith(notes);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackSongById', () => {
      it('Should return tracked Song primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackSongById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
