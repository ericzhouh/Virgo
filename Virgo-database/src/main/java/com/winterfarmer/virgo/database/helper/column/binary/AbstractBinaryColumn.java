package com.winterfarmer.virgo.database.helper.column.binary;

import com.winterfarmer.virgo.database.helper.column.Column;

/**
 * Created by yangtianhang on 15-3-7.
 */
public abstract class AbstractBinaryColumn extends Column {
    private final Integer length;

    public AbstractBinaryColumn(String name, int length) {
        super(name);
        this.length = length;
    }

    @Override
    protected String createDateTypeSubDefinition() {
        if (length != null) {
            return getDataTypeName() + "(" + length + ")";
        }

        return getDataTypeName();
    }

    public Integer getLength() {
        return length;
    }
}
