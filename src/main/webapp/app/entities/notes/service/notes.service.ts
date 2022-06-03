import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { INotes, getNotesIdentifier } from '../notes.model';

export type EntityResponseType = HttpResponse<INotes>;
export type EntityArrayResponseType = HttpResponse<INotes[]>;

@Injectable({ providedIn: 'root' })
export class NotesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/notes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(notes: INotes): Observable<EntityResponseType> {
    return this.http.post<INotes>(this.resourceUrl, notes, { observe: 'response' });
  }

  update(notes: INotes): Observable<EntityResponseType> {
    return this.http.put<INotes>(`${this.resourceUrl}/${getNotesIdentifier(notes) as string}`, notes, { observe: 'response' });
  }

  partialUpdate(notes: INotes): Observable<EntityResponseType> {
    return this.http.patch<INotes>(`${this.resourceUrl}/${getNotesIdentifier(notes) as string}`, notes, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<INotes>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<INotes[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addNotesToCollectionIfMissing(notesCollection: INotes[], ...notesToCheck: (INotes | null | undefined)[]): INotes[] {
    const notes: INotes[] = notesToCheck.filter(isPresent);
    if (notes.length > 0) {
      const notesCollectionIdentifiers = notesCollection.map(notesItem => getNotesIdentifier(notesItem)!);
      const notesToAdd = notes.filter(notesItem => {
        const notesIdentifier = getNotesIdentifier(notesItem);
        if (notesIdentifier == null || notesCollectionIdentifiers.includes(notesIdentifier)) {
          return false;
        }
        notesCollectionIdentifiers.push(notesIdentifier);
        return true;
      });
      return [...notesToAdd, ...notesCollection];
    }
    return notesCollection;
  }
}
