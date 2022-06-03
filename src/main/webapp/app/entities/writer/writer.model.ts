export interface IWriter {
  id?: string;
  name?: string | null;
  songId?: string | null;
}

export class Writer implements IWriter {
  constructor(public id?: string, public name?: string | null, public songId?: string | null) {}
}

export function getWriterIdentifier(writer: IWriter): string | undefined {
  return writer.id;
}
