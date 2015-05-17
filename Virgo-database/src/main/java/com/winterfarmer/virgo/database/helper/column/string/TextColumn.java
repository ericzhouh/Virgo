package com.winterfarmer.virgo.database.helper.column.string;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yangtianhang on 15/5/17.
 */
public class TextColumn extends AbstractStringColumn {
    public static final String DATA_TYPE_NAME = "TEXT";

    public TextColumn(String name) {
        super(name, 0);
    }

    @Override
    public String getDataTypeName() {
        return DATA_TYPE_NAME;
    }

    @Override
    public String getValue(ResultSet rs) throws SQLException {
        return rs.getString(getName());
    }
}
