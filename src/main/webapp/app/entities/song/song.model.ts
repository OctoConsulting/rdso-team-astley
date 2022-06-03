export interface ISong {
  id?: number;
  title?: string;
  writer?: string;
  performer?: string;
  length?: string;
  soundtrack?: string;
  trackNumber?: number;
  url?: string | null;
}

export class Song implements ISong {
  constructor(
    public id?: number,
    public title?: string,
    public writer?: string,
    public performer?: string,
    public length?: string,
    public soundtrack?: string,
    public trackNumber?: number,
    public url?: string | null
  ) {}
}

export function getSongIdentifier(song: ISong): number | undefined {
  return song.id;
}
