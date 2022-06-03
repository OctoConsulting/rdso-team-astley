import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { INotes, Notes } from '../notes.model';

import { NotesService } from './notes.service';

describe('Notes Service', () => {
  let service: NotesService;
  let httpMock: HttpTestingController;
  let elemDefault: INotes;
  let expectedResult: INotes | INotes[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(NotesService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      userId: 'AAAAAAA',
      songId: 'AAAAAAA',
      note: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find('9fec3727-3421-4967-b213-ba36557ca194').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Notes', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Notes()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Notes', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          userId: 'BBBBBB',
          songId: 'BBBBBB',
          note: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Notes', () => {
      const patchObject = Object.assign({}, new Notes());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Notes', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          userId: 'BBBBBB',
          songId: 'BBBBBB',
          note: 'BBBBBB',
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

    it('should delete a Notes', () => {
      service.delete('9fec3727-3421-4967-b213-ba36557ca194').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addNotesToCollectionIfMissing', () => {
      it('should add a Notes to an empty array', () => {
        const notes: INotes = { id: '9fec3727-3421-4967-b213-ba36557ca194' };
        expectedResult = service.addNotesToCollectionIfMissing([], notes);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(notes);
      });

      it('should not add a Notes to an array that contains it', () => {
        const notes: INotes = { id: '9fec3727-3421-4967-b213-ba36557ca194' };
        const notesCollection: INotes[] = [
          {
            ...notes,
          },
          { id: '1361f429-3817-4123-8ee3-fdf8943310b2' },
        ];
        expectedResult = service.addNotesToCollectionIfMissing(notesCollection, notes);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Notes to an array that doesn't contain it", () => {
        const notes: INotes = { id: '9fec3727-3421-4967-b213-ba36557ca194' };
        const notesCollection: INotes[] = [{ id: '1361f429-3817-4123-8ee3-fdf8943310b2' }];
        expectedResult = service.addNotesToCollectionIfMissing(notesCollection, notes);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(notes);
      });

      it('should add only unique Notes to an array', () => {
        const notesArray: INotes[] = [
          { id: '9fec3727-3421-4967-b213-ba36557ca194' },
          { id: '1361f429-3817-4123-8ee3-fdf8943310b2' },
          { id: 'c5662019-aa06-44b5-ad22-67c482e9add9' },
        ];
        const notesCollection: INotes[] = [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }];
        expectedResult = service.addNotesToCollectionIfMissing(notesCollection, ...notesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const notes: INotes = { id: '9fec3727-3421-4967-b213-ba36557ca194' };
        const notes2: INotes = { id: '1361f429-3817-4123-8ee3-fdf8943310b2' };
        expectedResult = service.addNotesToCollectionIfMissing([], notes, notes2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(notes);
        expect(expectedResult).toContain(notes2);
      });

      it('should accept null and undefined values', () => {
        const notes: INotes = { id: '9fec3727-3421-4967-b213-ba36557ca194' };
        expectedResult = service.addNotesToCollectionIfMissing([], null, notes, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(notes);
      });

      it('should return initial array if no Notes is added', () => {
        const notesCollection: INotes[] = [{ id: '9fec3727-3421-4967-b213-ba36557ca194' }];
        expectedResult = service.addNotesToCollectionIfMissing(notesCollection, undefined, null);
        expect(expectedResult).toEqual(notesCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
