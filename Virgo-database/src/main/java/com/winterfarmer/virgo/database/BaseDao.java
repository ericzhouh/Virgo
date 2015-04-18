package com.winterfarmer.virgo.database;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.winterfarmer.virgo.common.util.ArrayUtil;
import com.winterfarmer.virgo.common.util.ConfigUtil;
import com.winterfarmer.virgo.database.helper.column.Column;
import com.winterfarmer.virgo.log.VirgoLogger;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * Created by yangtianhang on 15-3-3.
 */
public class BaseDao {
    @Resource
    JdbcTemplateFactory jdbcTemplateFactory;

    protected static String dropDDL(String tableName) {
        return String.format("drop table if exists `%s`; ", tableName);
    }

    public void setJdbcTemplateFactory(JdbcTemplateFactory jdbcTemplateFactory) {
        this.jdbcTemplateFactory = jdbcTemplateFactory;
    }

    public JdbcTemplate getReadJdbcTemplate() {
        return jdbcTemplateFactory.getReadJdbcTemplate();
    }

    public JdbcTemplate getWriteJdbcTemplate() {
        return jdbcTemplateFactory.getWriteJdbcTemplate();
    }

    public NamedParameterJdbcTemplate getReadNamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(jdbcTemplateFactory.getReadJdbcTemplate());
    }

    public NamedParameterJdbcTemplate getWriteNamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(jdbcTemplateFactory.getWriteJdbcTemplate());
    }

    /**
     * 目前只有单元测试情况下才生效,防止对线上误操作.
     *
     * @param createDDL
     * @param dropDDL
     * @param dropBeforeCreate
     */
    public void initTable(String createDDL, String dropDDL, boolean dropBeforeCreate) {
        System.out.println(createDDL);
        if (ConfigUtil.isUnitTesting()) {
            if (dropBeforeCreate) {
                getWriteJdbcTemplate().execute(dropDDL);
            }
        }

        try {
            getWriteJdbcTemplate().execute(createDDL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void setExtForPreparedStatement(PreparedStatement ps, int position, Map<String, Object> properties) throws SQLException {
        if (MapUtils.isEmpty(properties)) {
            ps.setNull(position, Types.VARBINARY);
        } else {
            ps.setBytes(position, JSON.toJSONString(properties).getBytes(Charsets.UTF_8));
        }
    }

    protected <T> T queryForObject(JdbcTemplate jdbcTemplate, String sql, RowMapper<T> rowMapper, Object... args) {
        if (VirgoLogger.isDebugEnabled()) {
            VirgoLogger.debug("class:{}, queryForObject sql:{}, params:{}, rowmapper:{}", this.getClass().getSimpleName(), sql, JSON.toJSONString(args), rowMapper.getClass().getSimpleName());
        }

        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, args);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            if (VirgoLogger.isDebugEnabled()) {
                VirgoLogger.debug("class:{}, queryForObject sql:{} ,params:{}, rowmapper:{} , no_result", this.getClass().getSimpleName(), sql, JSON.toJSONString(args), rowMapper.getClass().getSimpleName());
            }
            return null;
        }
    }

    protected <T> ImmutableList<T> queryForList(JdbcTemplate jdbcTemplate, String sql, RowMapper<T> rowMapper, Object... args) {
        if (VirgoLogger.isDebugEnabled()) {
            VirgoLogger.debug("class:{}, queryForList sql:{}, params:{}, rowmapper:{}", this.getClass().getSimpleName(), sql, JSON.toJSONString(args), rowMapper.getClass().getSimpleName());
        }

        try {
            List<T> result = jdbcTemplate.query(sql, rowMapper, args);
            if (CollectionUtils.isNotEmpty(result)) {
                return ImmutableList.copyOf(result);
            }
        } catch (Exception e) {
            if (VirgoLogger.isDebugEnabled()) {
                VirgoLogger.debug("class:{}, queryForList sql:{}, params:{}, rowmapper:{}, exception:{}, no_result", this.getClass().getSimpleName(), sql, JSON.toJSONString(args), rowMapper.getClass().getSimpleName(), e.getMessage());
            }
        }

        return ImmutableList.of();
    }

    protected int update(String sql, Object... args) {
        if (VirgoLogger.isDebugEnabled()) {
            VirgoLogger.debug("class:{}, update sql:{}, params:{}", this.getClass().getSimpleName(), sql, JSON.toJSONString(args));
        }

        try {
            return getWriteJdbcTemplate().update(sql, args);
        } catch (Exception e) {
            if (VirgoLogger.isDebugEnabled()) {
                VirgoLogger.debug("class:{}, update sql:{}, params:{}, exception:{}, update failed", this.getClass().getSimpleName(), sql, JSON.toJSONString(args), e.getMessage());
            }

            return -1;
        }
    }

    protected static String insertIntoSQL(String tableName, Column column, Column... others) {
        String columnNames;
        if (ArrayUtils.isEmpty(others)) {
            columnNames = column.toString();
        } else {
            columnNames = column.toString() + "," + StringUtils.join(others, ",");
        }

        String values = StringUtils.repeat("?", ",", 1 + others.length);

        return "INSERT INTO " + tableName + " (" + columnNames + ") " + "VALUES" + "( " + values + " )";
    }

    protected static String updateSql(String tableName, Column column, Column... others) {
        String columnNames = column.eqWhich();
        for (Column c : others) {
            columnNames += "," + c.eqWhich();
        }

        return "UPDATE " + tableName + " set " + columnNames + " ";
    }

    protected static String deleteSql(String tableName) {
        return "DELETE from " + tableName + " ";
    }

    protected static String selectAllSql(String tableName) {
        return "SELECT * FROM " + tableName + " ";
    }

    protected static String selectSql(String tableName, Column column, Column... others) {
        String columnNames = column.toString();
        for (Column c : others) {
            columnNames += "," + c.toString();
        }

        return "SELECT " + columnNames + " FROM " + tableName + " ";
    }

    protected static class WhereClauseBuilder {
        String whereClause;
        boolean limit = false;
        boolean offset = false;

        public WhereClauseBuilder() {
            this.whereClause = "";
        }

        public WhereClauseBuilder(String firstSubClause) {
            this.whereClause = firstSubClause;
        }

        public WhereClauseBuilder or(String subClause) {
            this.whereClause += " or " + subClause;
            return this;
        }

        public WhereClauseBuilder and(String subClause) {
            this.whereClause += " and " + subClause;
            return this;
        }

        public WhereClauseBuilder limit() {
            this.limit = true;
            return this;
        }

        public WhereClauseBuilder offset() {
            this.offset = true;
            return this;
        }

        public WhereClauseBuilder limit(boolean limit) {
            this.limit = limit;
            return this;
        }

        public WhereClauseBuilder limitOffset() {
            this.offset = true;
            this.limit = true;
            return this;
        }


        public String build() {
            String whereClause;
            if (StringUtils.isBlank(this.whereClause)) {
                whereClause = "";
            } else {
                whereClause = " where " + this.whereClause + " ";
            }
            if (limit != false) {
                whereClause += " limit=? ";
            }

            if (offset != false) {
                whereClause += " offset=? ";
            }
            return whereClause;
        }

        @Override
        public String toString() {
            return build();
        }
    }

    protected static WhereClauseBuilder where(String firstSubClause) {
        return new WhereClauseBuilder(firstSubClause);
    }

    protected static WhereClauseBuilder where() {
        return new WhereClauseBuilder();
    }
}
