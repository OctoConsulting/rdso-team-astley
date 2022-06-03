import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { WriterDetailComponent } from './writer-detail.component';

describe('Writer Management Detail Component', () => {
  let comp: WriterDetailComponent;
  let fixture: ComponentFixture<WriterDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [WriterDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ writer: { id: '9fec3727-3421-4967-b213-ba36557ca194' } }) },
        },
      ],
    })
      .overrideTemplate(WriterDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(WriterDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load writer on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.writer).toEqual(expect.objectContaining({ id: '9fec3727-3421-4967-b213-ba36557ca194' }));
    });
  });
});
