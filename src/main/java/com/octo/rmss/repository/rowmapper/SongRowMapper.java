package com.octo.rmss.repository.rowmapper;

import com.octo.rmss.domain.Song;
import io.r2dbc.spi.Row;
import java.time.Duration;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Song}, with proper type conversions.
 */
@Service
public class SongRowMapper implements BiFunction<Row, String, Song> {

    private final ColumnConverter converter;

    public SongRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Song} stored in the database.
     */
    @Override
    public Song apply(Row row, String prefix) {
        Song entity = new Song();
        entity.setId(converter.fromRow(row, prefix + "_id", UUID.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setWriter(converter.fromRow(row, prefix + "_writer", String.class));
        entity.setPerformer(converter.fromRow(row, prefix + "_performer", String.class));
        entity.setLength(converter.fromRow(row, prefix + "_length", Duration.class));
        entity.setSoundtrack(converter.fromRow(row, prefix + "_soundtrack", String.class));
        entity.setTrackNumber(converter.fromRow(row, prefix + "_track_number", Integer.class));
        entity.setUrl(converter.fromRow(row, prefix + "_url", String.class));
        return entity;
    }
}
