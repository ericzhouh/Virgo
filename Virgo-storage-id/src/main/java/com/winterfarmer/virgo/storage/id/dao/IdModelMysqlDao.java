package com.winterfarmer.virgo.storage.id.dao;

import com.winterfarmer.virgo.database.BaseMysqlDao;
import com.winterfarmer.virgo.database.helper.column.numeric.BigintColumn;
import com.winterfarmer.virgo.storage.id.model.BaseIdModel;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    @Override
    public T insert(final T object) {
        if (object == null) {
            return null;
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int result = getWriteJdbcTemplate().update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        return createInsertPreparedStatement(connection, object);
                    }
                },
                keyHolder
        );

        if (result <= 0) {
            return null;
        } else {
            object.setId(keyHolder.getKey().longValue());
            return object;
        }
    }

    @Override
    public T update(T object) {
        if (object == null) {
            return null;
        }

        return doUpdate(object) > 0 ? object : null;
    }

    abstract protected PreparedStatement createInsertPreparedStatement(Connection connection, final T object) throws SQLException;

    abstract protected int doUpdate(T object);
}
