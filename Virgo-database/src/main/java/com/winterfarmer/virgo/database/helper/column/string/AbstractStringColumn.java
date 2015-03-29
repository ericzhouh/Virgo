package com.winterfarmer.virgo.database.helper.column.string;

import com.winterfarmer.virgo.database.helper.Charset;
import com.winterfarmer.virgo.database.helper.Collation;
import com.winterfarmer.virgo.database.helper.column.Column;

/**
 * Created by yangtianhang on 15-3-6.
 */
public abstract class AbstractStringColumn extends Column {
    private Charset charset;
    private Collation collation;
    private final Integer length;

    public AbstractStringColumn(String name, int length) {
        super(name);
        this.charset = Charset.utf8mb4;
        this.collation = Collation.utf8mb4_unicode_ci;
        this.length = length;
    }

    @Override
    protected String createDateTypeSubDefinition() {
        String charsetDefinition = " CHARACTER SET " + charset.getName() + " COLLATE " + collation.getName();
        if (length != null) {
            return getDataTypeName() + "(" + length + ")" + charsetDefinition;
        }

        return getDataTypeName() + charsetDefinition;
    }

    public Charset getCharset() {
        return charset;
    }

    public AbstractStringColumn setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public Collation getCollation() {
        return collation;
    }

    public AbstractStringColumn setCollation(Collation collation) {
        this.collation = collation;
        return this;
    }

    public Integer getLength() {
        return length;
    }
}
