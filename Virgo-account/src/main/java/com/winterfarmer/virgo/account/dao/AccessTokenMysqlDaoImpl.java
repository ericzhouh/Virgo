package com.winterfarmer.virgo.account.dao;

import com.winterfarmer.virgo.account.model.AccessToken;
import com.winterfarmer.virgo.database.BaseDao;
import com.winterfarmer.virgo.database.helper.IndexType;
import com.winterfarmer.virgo.database.helper.MysqlDDLBuilder;
import com.winterfarmer.virgo.database.helper.column.Columns;
import com.winterfarmer.virgo.database.helper.column.date.DatetimeColumn;
import com.winterfarmer.virgo.database.helper.column.date.TimeStampColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.BigintColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.SmallIntColumn;
import com.winterfarmer.virgo.database.helper.column.string.VarcharColumn;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created by yangtianhang on 15-3-26.
 */
@Repository(value = "accessTokenMysqlDao")
public class AccessTokenMysqlDaoImpl extends BaseDao implements AccessTokenDao {
    public static final String ACCESS_TOKEN_TABLE_NAME = "access_token";

    private static final BigintColumn userId = Columns.newUserIdColumn(false, false);
    private static final SmallIntColumn appKey = Columns.newAppKeyColumn(false);
    private static final VarcharColumn token = (VarcharColumn) new VarcharColumn("token", 128).
            setComment("token").setAllowNull(false);
    private static final DatetimeColumn expireAt = Columns.newExpireAtColumn(false);
    private static final TimeStampColumn createAt = Columns.newCreateAtColumn();

    public static final String createAccessTokenDDL = new MysqlDDLBuilder(ACCESS_TOKEN_TABLE_NAME).
            addColumn(userId).addColumn(appKey).
            addColumn(token).addColumn(createAt).
            addColumn(expireAt).
            setPrimaryKey(userId, appKey).
            addIndex(userId).
            addIndex(IndexType.btree, expireAt).
            addIndex(IndexType.btree, createAt).buildCreateDDL();

    public void initTable(boolean dropBeforeCreate) {
        super.initTable(createAccessTokenDDL, BaseDao.dropDDL(ACCESS_TOKEN_TABLE_NAME), dropBeforeCreate);
    }

    private static final String retrieve_user_sql =
            "select * from " + ACCESS_TOKEN_TABLE_NAME + " where " +
                    userId.eqWhich() + " and " +
                    appKey.eqWhich() + ";";

    public static final RowMapper<AccessToken> accessTokenRowMapper = new RowMapper<AccessToken>() {
        @Override
        public AccessToken mapRow(ResultSet rs, int rowNum) throws SQLException {
            AccessToken accessToken = new AccessToken();
            accessToken.setUserId(userId.getValue(rs));
            accessToken.setAppKey(appKey.getValue(rs));
            accessToken.setToken(token.getValue(rs));
            accessToken.setCreateAt(createAt.getValue(rs));
            accessToken.setExpireAt(expireAt.getValue(rs));

            return accessToken;
        }
    };

    @Override
    public AccessToken retrieveAccessToken(long userId, int appKey) {
        return queryForObject(getReadJdbcTemplate(), retrieve_user_sql, accessTokenRowMapper, userId, appKey);
    }

    private static final String insert_access_token_sql =
            insertIntoSQL(ACCESS_TOKEN_TABLE_NAME, userId, appKey, token, expireAt);

    @Override
    public boolean createAccessToken(long userId, int appKey, String accessToken, long expireAt) {
        return update(insert_access_token_sql, userId, appKey, accessToken, new Timestamp(expireAt)) > 0;
    }

    private static final String update_access_token_sql =
            updateSql(ACCESS_TOKEN_TABLE_NAME, token, expireAt)
                    + new WhereClauseBuilder(userId.eqWhich()).and(appKey.eqWhich()).build();

    @Override
    public boolean updateAccessToken(long userId, int appKey, String accessToken, long expireAt) {
        return update(update_access_token_sql, accessToken, new Timestamp(expireAt), userId, appKey) > 0;
    }

    private static final String delete_access_token_sql = deleteSql(ACCESS_TOKEN_TABLE_NAME) + where(userId.eqWhich());

    @Override
    public boolean deleteAccessToken(long userId) {
        return update(delete_access_token_sql, userId) > 0;
    }

    private static final String delete_app_access_token_sql = deleteSql(ACCESS_TOKEN_TABLE_NAME) +
            where(userId.eqWhich()).and(appKey.eqWhich());

    @Override
    public boolean deleteAccessToken(long userId, int appKey) {
        return update(delete_access_token_sql, userId, appKey) > 0;
    }
}
