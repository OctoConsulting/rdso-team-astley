import { ISong } from 'app/entities/song/song.model';

export interface INotes {
  id?: number;
  userId?: string | null;
  note?: string | null;
  song?: ISong | null;
}

export class Notes implements INotes {
  constructor(public id?: number, public userId?: string | null, public note?: string | null, public song?: ISong | null) {}
}

export function getNotesIdentifier(notes: INotes): number | undefined {
  return notes.id;
}
