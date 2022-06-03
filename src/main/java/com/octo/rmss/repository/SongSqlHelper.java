package com.octo.rmss.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class SongSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("title", table, columnPrefix + "_title"));
        columns.add(Column.aliased("performer", table, columnPrefix + "_performer"));
        columns.add(Column.aliased("length", table, columnPrefix + "_length"));
        columns.add(Column.aliased("soundtrack", table, columnPrefix + "_soundtrack"));
        columns.add(Column.aliased("track_number", table, columnPrefix + "_track_number"));
        columns.add(Column.aliased("url", table, columnPrefix + "_url"));
        columns.add(Column.aliased("writer", table, columnPrefix + "_writer"));

        return columns;
    }
}
