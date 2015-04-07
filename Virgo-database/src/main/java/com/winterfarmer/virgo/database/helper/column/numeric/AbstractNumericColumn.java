package com.winterfarmer.virgo.database.helper.column.numeric;

import com.winterfarmer.virgo.database.helper.column.Column;

/**
 * Created by yangtianhang on 15-3-5.
 */
public abstract class AbstractNumericColumn extends Column {
    private boolean isUnsigned;
    private int displaySize;
    private boolean isZeroFill;
    private boolean isAutoIncrease;

    public AbstractNumericColumn(String name) {
        super(name);
        setUnsigned(false);
        setAutoIncrease(false);
        setDisplaySize(0, false);
    }

    @Override
    protected String createDateTypeSubDefinition() {
        String dateTypeSubDefinition = getDataTypeName();
        if (displaySize != 0) {
            dateTypeSubDefinition += "(" + displaySize + ")";
        }

        if (isUnsigned) {
            dateTypeSubDefinition += " UNSIGNED ";
        }

        if (displaySize != 0 && isZeroFill) {
            dateTypeSubDefinition += " ZEROFILL ";
        }

        if (isAutoIncrease) {
            dateTypeSubDefinition += " AUTO_INCREMENT ";
        }

        return dateTypeSubDefinition;
    }

    public AbstractNumericColumn setUnsigned(boolean isUnsigned) {
        this.isUnsigned = isUnsigned;
        return this;
    }

    public AbstractNumericColumn setDisplaySize(int displaySize) {
        this.displaySize = displaySize;
        return this;
    }

    public AbstractNumericColumn setDisplaySize(int displaySize, boolean isZeroFill) {
        this.displaySize = displaySize;
        this.isZeroFill = isZeroFill;
        return this;
    }

    public boolean isAutoIncrease() {
        return isAutoIncrease;
    }

    public AbstractNumericColumn setAutoIncrease(boolean isAutoIncrease) {
        this.isAutoIncrease = isAutoIncrease;
        return this;
    }

    public boolean isUnsigned() {
        return isUnsigned;
    }

    public int getDisplaySize() {
        return displaySize;
    }

    public boolean isZeroFill() {
        return isZeroFill;
    }
}
