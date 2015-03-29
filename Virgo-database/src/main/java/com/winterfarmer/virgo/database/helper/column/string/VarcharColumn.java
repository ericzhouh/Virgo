package com.winterfarmer.virgo.database.helper.column.string;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yangtianhang on 15-3-6.
 */
public class VarcharColumn extends AbstractStringColumn {
    public static final String DATA_TYPE_NAME = "VARCHAR";

    public VarcharColumn(String name, int length) {
        super(name, length);
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
