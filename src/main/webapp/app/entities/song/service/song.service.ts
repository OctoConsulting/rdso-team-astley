import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISong, getSongIdentifier } from '../song.model';

export type EntityResponseType = HttpResponse<ISong>;
export type EntityArrayResponseType = HttpResponse<ISong[]>;

@Injectable({ providedIn: 'root' })
export class SongService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/songs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(song: ISong): Observable<EntityResponseType> {
    return this.http.post<ISong>(this.resourceUrl, song, { observe: 'response' });
  }

  update(song: ISong): Observable<EntityResponseType> {
    return this.http.put<ISong>(`${this.resourceUrl}/${getSongIdentifier(song) as number}`, song, { observe: 'response' });
  }

  partialUpdate(song: ISong): Observable<EntityResponseType> {
    return this.http.patch<ISong>(`${this.resourceUrl}/${getSongIdentifier(song) as number}`, song, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISong>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISong[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSongToCollectionIfMissing(songCollection: ISong[], ...songsToCheck: (ISong | null | undefined)[]): ISong[] {
    const songs: ISong[] = songsToCheck.filter(isPresent);
    if (songs.length > 0) {
      const songCollectionIdentifiers = songCollection.map(songItem => getSongIdentifier(songItem)!);
      const songsToAdd = songs.filter(songItem => {
        const songIdentifier = getSongIdentifier(songItem);
        if (songIdentifier == null || songCollectionIdentifiers.includes(songIdentifier)) {
          return false;
        }
        songCollectionIdentifiers.push(songIdentifier);
        return true;
      });
      return [...songsToAdd, ...songCollection];
    }
    return songCollection;
  }
}
