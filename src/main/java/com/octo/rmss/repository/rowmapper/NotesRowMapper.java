package com.octo.rmss.repository.rowmapper;

import com.octo.rmss.domain.Notes;
import io.r2dbc.spi.Row;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Notes}, with proper type conversions.
 */
@Service
public class NotesRowMapper implements BiFunction<Row, String, Notes> {

    private final ColumnConverter converter;

    public NotesRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Notes} stored in the database.
     */
    @Override
    public Notes apply(Row row, String prefix) {
        Notes entity = new Notes();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", UUID.class));
        entity.setSongId(converter.fromRow(row, prefix + "_song_id", UUID.class));
        entity.setNote(converter.fromRow(row, prefix + "_note", String.class));
        return entity;
    }
}
