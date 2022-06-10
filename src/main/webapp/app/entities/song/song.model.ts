import { INotes } from 'app/entities/notes/notes.model';

export interface ISong {
  id?: number;
  title?: string | null;
  performer?: string | null;
  length?: string | null;
  soundtrack?: string | null;
  trackNumber?: number | null;
  url?: string | null;
  writer?: string | null;
  notes?: INotes[] | null;
}

export class Song implements ISong {
  constructor(
    public id?: number,
    public title?: string | null,
    public performer?: string | null,
    public length?: string | null,
    public soundtrack?: string | null,
    public trackNumber?: number | null,
    public url?: string | null,
    public writer?: string | null,
    public notes?: INotes[] | null
  ) {}
}

export function getSongIdentifier(song: ISong): number | undefined {
  return song.id;
}
