import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { WriterService } from '../service/writer.service';

import { WriterComponent } from './writer.component';

describe('Writer Management Component', () => {
  let comp: WriterComponent;
  let fixture: ComponentFixture<WriterComponent>;
  let service: WriterService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [WriterComponent],
    })
      .overrideTemplate(WriterComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(WriterComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(WriterService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.writers?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
