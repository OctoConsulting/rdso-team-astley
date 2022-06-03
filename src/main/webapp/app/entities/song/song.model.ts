export interface ISong {
  id?: string;
  title?: string | null;
  writer?: string | null;
  performer?: string | null;
  length?: string | null;
  soundtrack?: string | null;
  trackNumber?: number | null;
  url?: string | null;
}

export class Song implements ISong {
  constructor(
    public id?: string,
    public title?: string | null,
    public writer?: string | null,
    public performer?: string | null,
    public length?: string | null,
    public soundtrack?: string | null,
    public trackNumber?: number | null,
    public url?: string | null
  ) {}
}

export function getSongIdentifier(song: ISong): string | undefined {
  return song.id;
}
