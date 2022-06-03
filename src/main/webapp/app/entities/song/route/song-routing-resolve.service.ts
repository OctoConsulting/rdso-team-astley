import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISong, Song } from '../song.model';
import { SongService } from '../service/song.service';

@Injectable({ providedIn: 'root' })
export class SongRoutingResolveService implements Resolve<ISong> {
  constructor(protected service: SongService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISong> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((song: HttpResponse<Song>) => {
          if (song.body) {
            return of(song.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Song());
  }
}
