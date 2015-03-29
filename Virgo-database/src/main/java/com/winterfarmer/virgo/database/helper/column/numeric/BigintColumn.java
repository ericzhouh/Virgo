package com.winterfarmer.virgo.database.helper.column.numeric;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yangtianhang on 15-3-5.
 */
public class BigintColumn extends AbstractNumericColumn {
    public static final String DATA_TYPE_NAME = "BIGINT";

    public BigintColumn(String name) {
        super(name);
    }

    @Override
    public String getDataTypeName() {
        return DATA_TYPE_NAME;
    }

    @Override
    public Long getValue(ResultSet rs) throws SQLException {
        return rs.getLong(getName());
    }
}
