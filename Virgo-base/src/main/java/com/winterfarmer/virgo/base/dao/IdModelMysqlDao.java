package com.winterfarmer.virgo.base.dao;

import com.winterfarmer.virgo.base.model.BaseIdModel;
import com.winterfarmer.virgo.database.BaseMysqlDao;
import com.winterfarmer.virgo.database.helper.column.numeric.BigintColumn;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.List;

/**
 * Created by yangtianhang on 15/5/13.
 */
public abstract class IdModelMysqlDao<T extends BaseIdModel> extends BaseMysqlDao implements IdModelDao<T> {
    private final String tableName;
    private final BigintColumn idColumn;
    private final String selectAllInIdsSql;
    private final RowMapper<T> rowMapper;
    private final String retrieveSql;

    public IdModelMysqlDao(String tableName, BigintColumn idColumn, RowMapper<T> rowMapper) {
        this.tableName = tableName;
        this.idColumn = idColumn;
        this.selectAllInIdsSql = "select * from " + tableName + " where " + this.idColumn.getName() + " in (ids:)";
        this.rowMapper = rowMapper;
        this.retrieveSql = "select * from " + this.tableName + " where " + this.idColumn.eqWhich() + ";";
    }

    @Override
    public T get(long id) {
        return queryForObject(getReadJdbcTemplate(), retrieveSql, rowMapper, id);
    }

    @Override
    public List<T> list(long... ids) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", ids);
        return queryForList(getReadJdbcTemplate(), selectAllInIdsSql, rowMapper, parameters);
    }
}
