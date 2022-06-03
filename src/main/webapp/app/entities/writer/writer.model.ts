export interface IWriter {
  id?: number;
  name?: string | null;
  songId?: number | null;
}

export class Writer implements IWriter {
  constructor(public id?: number, public name?: string | null, public songId?: number | null) {}
}

export function getWriterIdentifier(writer: IWriter): number | undefined {
  return writer.id;
}
