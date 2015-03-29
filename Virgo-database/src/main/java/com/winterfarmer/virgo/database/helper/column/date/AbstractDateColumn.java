package com.winterfarmer.virgo.database.helper.column.date;

import com.winterfarmer.virgo.database.helper.column.Column;

/**
 * Created by yangtianhang on 15-3-5.
 */
public abstract class AbstractDateColumn extends Column {
    public AbstractDateColumn(String name) {
        super(name);
    }

    @Override
    protected String createDateTypeSubDefinition() {
        return getDataTypeName();
    }
}
