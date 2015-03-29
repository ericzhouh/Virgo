package com.winterfarmer.virgo.database.helper.column.binary;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yangtianhang on 15-3-7.
 */
public class VarbinaryColumn extends AbstractBinaryColumn {
    public static final String DATA_TYPE_NAME = "VARBINARY";

    public VarbinaryColumn(String name, int length) {
        super(name, length);
    }

    @Override
    public String getDataTypeName() {
        return DATA_TYPE_NAME;
    }

    @Override
    public byte[] getValue(ResultSet rs) throws SQLException {
        return rs.getBytes(getName());
    }
}
