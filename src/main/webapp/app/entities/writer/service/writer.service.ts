import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IWriter, getWriterIdentifier } from '../writer.model';

export type EntityResponseType = HttpResponse<IWriter>;
export type EntityArrayResponseType = HttpResponse<IWriter[]>;

@Injectable({ providedIn: 'root' })
export class WriterService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/writers');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(writer: IWriter): Observable<EntityResponseType> {
    return this.http.post<IWriter>(this.resourceUrl, writer, { observe: 'response' });
  }

  update(writer: IWriter): Observable<EntityResponseType> {
    return this.http.put<IWriter>(`${this.resourceUrl}/${getWriterIdentifier(writer) as number}`, writer, { observe: 'response' });
  }

  partialUpdate(writer: IWriter): Observable<EntityResponseType> {
    return this.http.patch<IWriter>(`${this.resourceUrl}/${getWriterIdentifier(writer) as number}`, writer, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IWriter>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IWriter[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addWriterToCollectionIfMissing(writerCollection: IWriter[], ...writersToCheck: (IWriter | null | undefined)[]): IWriter[] {
    const writers: IWriter[] = writersToCheck.filter(isPresent);
    if (writers.length > 0) {
      const writerCollectionIdentifiers = writerCollection.map(writerItem => getWriterIdentifier(writerItem)!);
      const writersToAdd = writers.filter(writerItem => {
        const writerIdentifier = getWriterIdentifier(writerItem);
        if (writerIdentifier == null || writerCollectionIdentifiers.includes(writerIdentifier)) {
          return false;
        }
        writerCollectionIdentifiers.push(writerIdentifier);
        return true;
      });
      return [...writersToAdd, ...writerCollection];
    }
    return writerCollection;
  }
}
