export interface INotes {
  id?: string;
  userId?: string | null;
  songId?: string | null;
  note?: string | null;
}

export class Notes implements INotes {
  constructor(public id?: string, public userId?: string | null, public songId?: string | null, public note?: string | null) {}
}

export function getNotesIdentifier(notes: INotes): string | undefined {
  return notes.id;
}
