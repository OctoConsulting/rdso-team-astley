import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { WriterService } from '../service/writer.service';
import { IWriter, Writer } from '../writer.model';

import { WriterUpdateComponent } from './writer-update.component';

describe('Writer Management Update Component', () => {
  let comp: WriterUpdateComponent;
  let fixture: ComponentFixture<WriterUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let writerService: WriterService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [WriterUpdateComponent],
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
      .overrideTemplate(WriterUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(WriterUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    writerService = TestBed.inject(WriterService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const writer: IWriter = { id: 456 };

      activatedRoute.data = of({ writer });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(writer));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Writer>>();
      const writer = { id: 123 };
      jest.spyOn(writerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ writer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: writer }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(writerService.update).toHaveBeenCalledWith(writer);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Writer>>();
      const writer = new Writer();
      jest.spyOn(writerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ writer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: writer }));
      saveSubject.complete();

      // THEN
      expect(writerService.create).toHaveBeenCalledWith(writer);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Writer>>();
      const writer = { id: 123 };
      jest.spyOn(writerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ writer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(writerService.update).toHaveBeenCalledWith(writer);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
