package com.winterfarmer.virgo.database.helper.column;

import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yangtianhang on 15-3-5.
 */
public abstract class Column {
    private final String name;
    private boolean allowNull;
    private boolean isUnique;
    private String comment;
    private String defaultValue;

    public Column(String name) {
        this.name = name;
        setAllowNull(false);
        setUnique(false);
        setComment(null);
        setDefaultValue(null);
    }

    abstract public String getDataTypeName();

    abstract protected String createDateTypeSubDefinition();

    abstract public Object getValue(ResultSet rs) throws SQLException;

    public String createColumnDefinitionString() {
        String columnDefinitionString = String.format(" `%s` ", getName());
        columnDefinitionString += createDateTypeSubDefinition();
        if (!allowNull) {
            columnDefinitionString += " NOT NULL ";
        }
        if (StringUtils.isNotEmpty(defaultValue)) {
            columnDefinitionString += " DEFAULT " + defaultValue + " ";
        }
        if (StringUtils.isNoneBlank(comment)) {
            columnDefinitionString += " COMMENT '" + comment + "' ";
        }

        return columnDefinitionString;
    }

    public Column setAllowNull(boolean allowNull) {
        this.allowNull = allowNull;
        return this;
    }

    public Column setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public Column setUnique(boolean isUnique) {
        this.isUnique = isUnique;
        return this;
    }

    public Column setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getName() {
        return name;
    }

    public boolean isAllowNull() {
        return allowNull;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public String getComment() {
        return comment;
    }

    public String eqWhich() {
        return getName() + "=? ";
    }
}
