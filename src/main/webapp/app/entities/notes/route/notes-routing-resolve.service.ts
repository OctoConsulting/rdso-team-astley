import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { INotes, Notes } from '../notes.model';
import { NotesService } from '../service/notes.service';

@Injectable({ providedIn: 'root' })
export class NotesRoutingResolveService implements Resolve<INotes> {
  constructor(protected service: NotesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INotes> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((notes: HttpResponse<Notes>) => {
          if (notes.body) {
            return of(notes.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Notes());
  }
}
