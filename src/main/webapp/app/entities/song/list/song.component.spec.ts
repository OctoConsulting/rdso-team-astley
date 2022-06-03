import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { SongService } from '../service/song.service';

import { SongComponent } from './song.component';

describe('Song Management Component', () => {
  let comp: SongComponent;
  let fixture: ComponentFixture<SongComponent>;
  let service: SongService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [SongComponent],
    })
      .overrideTemplate(SongComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SongComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SongService);

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
    expect(comp.songs?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
