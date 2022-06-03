export interface INotes {
  id?: number;
  userId?: string | null;
  songId?: number | null;
  note?: string | null;
}

export class Notes implements INotes {
  constructor(public id?: number, public userId?: string | null, public songId?: number | null, public note?: string | null) {}
}

export function getNotesIdentifier(notes: INotes): number | undefined {
  return notes.id;
}
