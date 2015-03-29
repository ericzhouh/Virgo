package com.winterfarmer.virgo.database.helper.column.date;

import com.winterfarmer.virgo.database.helper.column.Column;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yangtianhang on 15-3-5.
 */
public class TimeStampColumn extends AbstractDateColumn {
    public static final String DATA_TYPE_NAME = "TIMESTAMP";

    public TimeStampColumn(String name) {
        super(name);
    }

    @Override
    public String getDataTypeName() {
        return DATA_TYPE_NAME;
    }

    public Column setCurrentTimestamp() {
        return super.setDefaultValue("CURRENT_TIMESTAMP");
    }

    @Override
    public Long getValue(ResultSet rs) throws SQLException {
        return rs.getTimestamp(getName()).getTime();
    }
}
