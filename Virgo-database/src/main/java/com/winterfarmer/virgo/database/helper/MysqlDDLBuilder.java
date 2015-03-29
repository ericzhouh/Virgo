package com.winterfarmer.virgo.database.helper;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.winterfarmer.virgo.common.util.ParamChecker;
import com.winterfarmer.virgo.database.helper.column.Column;
import com.winterfarmer.virgo.database.helper.column.numeric.BigintColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.IntColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.MediumintColumn;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yangtianhang on 15-3-5.
 */
public class MysqlDDLBuilder {
    public final int DEFAULT_AUTO_INCREMENT = 10000;
    private final String tableName;
    private final Map<String, Column> columnMap = Collections.synchronizedMap(Maps.<String, Column>newLinkedHashMap());
    private List<Column> primaryKeyList;
    private IndexType primaryKeyIndexType;
    private Engine engine;
    private int autoIncrement = DEFAULT_AUTO_INCREMENT;
    private Charset charset = Charset.utf8mb4;
    private Collation collation = Collation.utf8mb4_unicode_ci;
    private Set<Index> indexSet = Collections.synchronizedSet(Sets.<Index>newLinkedHashSet());
    private Set<String> indexNameSet = Sets.newHashSet();

    private class Index {
        private final String idxName;
        private final IndexType indexType;
        private final Set<String> indexColNameSet = Sets.newTreeSet();
        private final String indexColumnsString;

        Index(String idxName, Column column, Column... rest) {
            this(idxName, IndexType.hash, column, rest);
        }

        Index(Column column) {
            this(IndexType.hash, column);
        }

        Index(String idxName, IndexType indexType, Column column, Column... rest) {
            this.idxName = convertIdxName(idxName);
            this.indexType = indexType;

            indexColNameSet.add(column.getName());
            for (Column column1 : rest) {
                if (indexColNameSet.contains(column1)) {
                    throw new CreateDDLException("Index[" + this.idxName + "] column cannot be duplicate: " + column1.getName());
                }
                indexColNameSet.add(column1.getName());
            }

            this.indexColumnsString = createIndexColumnsString(indexColNameSet);
        }

        Index(IndexType indexType, Column column) {
            this.idxName = convertIdxName(column.getName());
            this.indexType = indexType;
            this.indexColNameSet.add(column.getName());
            this.indexColumnsString = createIndexColumnsString(indexColNameSet);
        }

        private String convertIdxName(String idxName) {
            if (StringUtils.startsWith(idxName, "idx_")) {
                return idxName;
            } else {
                return "idx_" + idxName;
            }
        }

        public String getIdxName() {
            return idxName;
        }

        public String getIndexColumnsString() {
            return indexColumnsString;
        }

        public String createIndexSentence() {
//            | {INDEX|KEY} [index_name] [index_type] (index_col_name,...)
            return "INDEX " + idxName + " USING " + indexType.getName() + " (" + indexColumnsString + " )";
        }

        private String createIndexColumnsString(Set<String> indexColNameSet) {
            return StringUtils.join(indexColNameSet, ",");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Index index = (Index) o;

            if (!indexColumnsString.equals(index.indexColumnsString)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return indexColumnsString.hashCode();
        }
    }

    public MysqlDDLBuilder(String tableName) {
        this.tableName = tableName;
        this.engine = Engine.InnoDB;
    }

    public MysqlDDLBuilder addColumn(Column column) {
        checkColumnNotNull(column);
        if (columnMap.containsKey(column.getName())) {
            throw new CreateDDLException("Column " + column.getName() + " has been added");
        }
        columnMap.put(column.getName(), column);
        return this;
    }

    public MysqlDDLBuilder addIndex(String idxName, IndexType indexType, Column column, Column... rest) {
        Index index = new Index(idxName, indexType, column, rest);
        return addIndex(index);
    }

    public MysqlDDLBuilder addIndex(String idxName, Column column, Column... rest) {
        Index index = new Index(idxName, column, rest);
        return addIndex(index);
    }

    public MysqlDDLBuilder addIndex(IndexType indexType, Column column) {
        Index index = new Index(indexType, column);
        return addIndex(index);
    }

    public MysqlDDLBuilder addIndex(Column column) {
        Index index = new Index(column);
        return addIndex(index);
    }

    public MysqlDDLBuilder setPrimaryKey(IndexType indexType, Column column, Column... rest) {
        checkColumnNotNull(column, rest);
        checkColumnAdded(column, rest);

        this.primaryKeyIndexType = indexType;

        if (CollectionUtils.isNotEmpty(primaryKeyList)) {
            throw new CreateDDLException("Cannot set primary key more than once");
        }

        this.primaryKeyList = Lists.asList(column, rest);

        return this;
    }

    public MysqlDDLBuilder setPrimaryKey(Column column, Column... rest) {
        return setPrimaryKey(IndexType.hash, column, rest);
    }

    public MysqlDDLBuilder setEngine(Engine engine) {
        ParamChecker.notNull(engine, "engine");
        this.engine = engine;
        return this;
    }

    public MysqlDDLBuilder setAutoIncrement(int autoIncrement) {
        ParamChecker.ge(autoIncrement, "autoIncrement", 0);
        this.autoIncrement = autoIncrement;
        return this;
    }

    public MysqlDDLBuilder setCharset(Charset charset) {
        ParamChecker.notNull(charset, "charset");
        this.charset = charset;
        return this;
    }

    public MysqlDDLBuilder setCollation(Collation collation) {
        ParamChecker.notNull(collation, "collation");
        this.collation = collation;
        return this;
    }

    public String buildCreateDDL() {
        StringBuilder ddl = new StringBuilder();
        ddl = buildTableName(ddl);
        ddl = ddl.append("(\n");
        ddl = buildColumns(ddl);
        ddl = buildPrimaryKey(ddl);
        ddl = buildIndex(ddl);
        ddl = ddl.append(")");
        ddl = buildTableOption(ddl);
        ddl.append(";\n");
        return ddl.toString();
    }

    public String buildRetrieveSQL() {
        if (CollectionUtils.isNotEmpty(primaryKeyList) && primaryKeyList.size() == 1) {
            Column primaryColumn = primaryKeyList.get(0);
            return "select * from " + getTableName() + " where " + primaryColumn.getName() + "=?;";
        }

        throw new CreateDDLException("function buildRetrieve can only support one primary key");
    }

    public String getTableName() {
        return tableName;
    }

    private StringBuilder buildTableName(StringBuilder ddl) {
        return ddl.append("CREATE TABLE ").append("`").append(getTableName()).append("`\n");
    }

    private StringBuilder buildColumns(StringBuilder ddl) {
        if (MapUtils.isEmpty(columnMap)) {
            throw new CreateDDLException("no column definition");
        }

        for (Map.Entry<String, Column> entry : columnMap.entrySet()) {
            ddl.append(entry.getValue().createColumnDefinitionString()).append(",\n");
        }

        return ddl;
    }

    private StringBuilder buildPrimaryKey(StringBuilder ddl) {
        if (CollectionUtils.isEmpty(primaryKeyList)) {
            throw new CreateDDLException("primary key has not been set");
        }

        List<String> primaryKeyNameList = Lists.transform(primaryKeyList, new Function<Column, String>() {
            @Override
            public String apply(Column column) {
                return "`" + column.getName() + "`";
            }
        });

        return ddl.append(String.format("PRIMARY KEY (%s) USING %s ", StringUtils.join(primaryKeyNameList, ","), primaryKeyIndexType.getName()));
    }

    private StringBuilder buildIndex(StringBuilder ddl) {
        if (CollectionUtils.isNotEmpty(indexSet)) {
            for (Index index : indexSet) {
                ddl.append(",\n").append(index.createIndexSentence());
            }
        }

        return ddl;
    }

    private StringBuilder buildTableOption(StringBuilder ddl) {
        ddl.append(String.format(" ENGINE=%s DEFAULT CHARSET=%s COLLATE=%s ", engine.getName(), charset.getName(), collation.getName()));
        if (primaryKeyList.size() == 1) {
            Column primaryKey = primaryKeyList.get(0);
            if (primaryKey instanceof BigintColumn || primaryKey instanceof IntColumn || primaryKey instanceof MediumintColumn) {
                ddl.append(" AUTO_INCREMENT=").append(autoIncrement);
            }
        }

        return ddl.append(";\n");
    }

    private MysqlDDLBuilder addIndex(Index index) {
        if (indexNameSet.contains(index.getIdxName())) {
            throw new CreateDDLException("Index name cannot be same: " + index.getIdxName());
        }

        if (indexSet.contains(index)) {
            throw new CreateDDLException("Index columns cannot be same: " + index.getIndexColumnsString());
        }

        indexNameSet.add(index.getIdxName());
        indexSet.add(index);

        return this;
    }

    private void checkColumnAdded(Column column, Column... rest) {
        checkColumnAdded(column);
        for (Column column1 : rest) {
            checkColumnAdded(column1);
        }
    }

    private void checkColumnAdded(Column column) {
        Column col = columnMap.get(column.getName());
        if (col == null || col != col) {
            throw new CreateDDLException("Column " + column.getName() + "[" + column + "]" + " is not added");
        }
    }

    private void checkColumnNotNull(Column column, Column... rest) {
        checkColumnNotNull(column);
        for (Column column1 : rest) {
            checkColumnNotNull(column1);
        }
    }

    private void checkColumnNotNull(Column column) {
        ParamChecker.notNull(column, "column");
    }
}
