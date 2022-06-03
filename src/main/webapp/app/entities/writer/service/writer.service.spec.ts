import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IWriter, Writer } from '../writer.model';

import { WriterService } from './writer.service';

describe('Writer Service', () => {
  let service: WriterService;
  let httpMock: HttpTestingController;
  let elemDefault: IWriter;
  let expectedResult: IWriter | IWriter[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(WriterService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      songId: 0,
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

    it('should create a Writer', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Writer()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Writer', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          songId: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Writer', () => {
      const patchObject = Object.assign(
        {
          songId: 1,
        },
        new Writer()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Writer', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          songId: 1,
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

    it('should delete a Writer', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addWriterToCollectionIfMissing', () => {
      it('should add a Writer to an empty array', () => {
        const writer: IWriter = { id: 123 };
        expectedResult = service.addWriterToCollectionIfMissing([], writer);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(writer);
      });

      it('should not add a Writer to an array that contains it', () => {
        const writer: IWriter = { id: 123 };
        const writerCollection: IWriter[] = [
          {
            ...writer,
          },
          { id: 456 },
        ];
        expectedResult = service.addWriterToCollectionIfMissing(writerCollection, writer);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Writer to an array that doesn't contain it", () => {
        const writer: IWriter = { id: 123 };
        const writerCollection: IWriter[] = [{ id: 456 }];
        expectedResult = service.addWriterToCollectionIfMissing(writerCollection, writer);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(writer);
      });

      it('should add only unique Writer to an array', () => {
        const writerArray: IWriter[] = [{ id: 123 }, { id: 456 }, { id: 99959 }];
        const writerCollection: IWriter[] = [{ id: 123 }];
        expectedResult = service.addWriterToCollectionIfMissing(writerCollection, ...writerArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const writer: IWriter = { id: 123 };
        const writer2: IWriter = { id: 456 };
        expectedResult = service.addWriterToCollectionIfMissing([], writer, writer2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(writer);
        expect(expectedResult).toContain(writer2);
      });

      it('should accept null and undefined values', () => {
        const writer: IWriter = { id: 123 };
        expectedResult = service.addWriterToCollectionIfMissing([], null, writer, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(writer);
      });

      it('should return initial array if no Writer is added', () => {
        const writerCollection: IWriter[] = [{ id: 123 }];
        expectedResult = service.addWriterToCollectionIfMissing(writerCollection, undefined, null);
        expect(expectedResult).toEqual(writerCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
