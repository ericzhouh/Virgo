package com.winterfarmer.virgo.database.helper.column.date;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yangtianhang on 15-3-5.
 */
public class DatetimeColumn extends AbstractDateColumn {
    public static final String DATA_TYPE_NAME = "DATETIME";

    public DatetimeColumn(String name) {
        super(name);
    }

    @Override
    public String getDataTypeName() {
        return DATA_TYPE_NAME;
    }

    @Override
    public Long getValue(ResultSet rs) throws SQLException {
        return rs.getTimestamp(getName()).getTime();
    }
}
