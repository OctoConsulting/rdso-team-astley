package com.octo.rmss.repository.rowmapper;

import com.octo.rmss.domain.Writer;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Writer}, with proper type conversions.
 */
@Service
public class WriterRowMapper implements BiFunction<Row, String, Writer> {

    private final ColumnConverter converter;

    public WriterRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Writer} stored in the database.
     */
    @Override
    public Writer apply(Row row, String prefix) {
        Writer entity = new Writer();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setSongId(converter.fromRow(row, prefix + "_song_id", Long.class));
        return entity;
    }
}
