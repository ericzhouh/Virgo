package com.winterfarmer.virgo.database.helper.column.date;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by yangtianhang on 15-3-5.
 */
public class DateColumn extends AbstractDateColumn {
    public static final String DATA_TYPE_NAME = "DATE";

    public DateColumn(String name) {
        super(name);
    }

    @Override
    public String getDataTypeName() {
        return DATA_TYPE_NAME;
    }

    @Override
    public Date getValue(ResultSet rs) throws SQLException {
        return rs.getDate(getName());
    }
}
