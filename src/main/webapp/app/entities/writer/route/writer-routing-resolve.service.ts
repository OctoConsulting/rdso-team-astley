import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IWriter, Writer } from '../writer.model';
import { WriterService } from '../service/writer.service';

@Injectable({ providedIn: 'root' })
export class WriterRoutingResolveService implements Resolve<IWriter> {
  constructor(protected service: WriterService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IWriter> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((writer: HttpResponse<Writer>) => {
          if (writer.body) {
            return of(writer.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Writer());
  }
}
