import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISong, Song } from '../song.model';

import { SongService } from './song.service';

describe('Song Service', () => {
  let service: SongService;
  let httpMock: HttpTestingController;
  let elemDefault: ISong;
  let expectedResult: ISong | ISong[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SongService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      title: 'AAAAAAA',
      writer: 'AAAAAAA',
      performer: 'AAAAAAA',
      length: 'PT1S',
      soundtrack: 'AAAAAAA',
      trackNumber: 0,
      url: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Song', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Song()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Song', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          title: 'BBBBBB',
          writer: 'BBBBBB',
          performer: 'BBBBBB',
          length: 'BBBBBB',
          soundtrack: 'BBBBBB',
          trackNumber: 1,
          url: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Song', () => {
      const patchObject = Object.assign(
        {
          performer: 'BBBBBB',
          length: 'BBBBBB',
          soundtrack: 'BBBBBB',
          trackNumber: 1,
          url: 'BBBBBB',
        },
        new Song()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Song', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          title: 'BBBBBB',
          writer: 'BBBBBB',
          performer: 'BBBBBB',
          length: 'BBBBBB',
          soundtrack: 'BBBBBB',
          trackNumber: 1,
          url: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Song', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSongToCollectionIfMissing', () => {
      it('should add a Song to an empty array', () => {
        const song: ISong = { id: 123 };
        expectedResult = service.addSongToCollectionIfMissing([], song);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(song);
      });

      it('should not add a Song to an array that contains it', () => {
        const song: ISong = { id: 123 };
        const songCollection: ISong[] = [
          {
            ...song,
          },
          { id: 456 },
        ];
        expectedResult = service.addSongToCollectionIfMissing(songCollection, song);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Song to an array that doesn't contain it", () => {
        const song: ISong = { id: 123 };
        const songCollection: ISong[] = [{ id: 456 }];
        expectedResult = service.addSongToCollectionIfMissing(songCollection, song);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(song);
      });

      it('should add only unique Song to an array', () => {
        const songArray: ISong[] = [{ id: 123 }, { id: 456 }, { id: 2009 }];
        const songCollection: ISong[] = [{ id: 123 }];
        expectedResult = service.addSongToCollectionIfMissing(songCollection, ...songArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const song: ISong = { id: 123 };
        const song2: ISong = { id: 456 };
        expectedResult = service.addSongToCollectionIfMissing([], song, song2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(song);
        expect(expectedResult).toContain(song2);
      });

      it('should accept null and undefined values', () => {
        const song: ISong = { id: 123 };
        expectedResult = service.addSongToCollectionIfMissing([], null, song, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(song);
      });

      it('should return initial array if no Song is added', () => {
        const songCollection: ISong[] = [{ id: 123 }];
        expectedResult = service.addSongToCollectionIfMissing(songCollection, undefined, null);
        expect(expectedResult).toEqual(songCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
