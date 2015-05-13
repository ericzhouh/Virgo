package com.winterfarmer.virgo.account.dao;

import com.winterfarmer.virgo.account.model.AccessToken;
import com.winterfarmer.virgo.database.BaseMysqlDao;
import com.winterfarmer.virgo.database.helper.MysqlDDLBuilder;
import com.winterfarmer.virgo.database.helper.column.Columns;
import com.winterfarmer.virgo.database.helper.column.date.DatetimeColumn;
import com.winterfarmer.virgo.database.helper.column.date.TimeStampColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.BigintColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.SmallIntColumn;
import com.winterfarmer.virgo.database.helper.column.string.VarcharColumn;
import org.springframework.stereotype.Repository;

/**
 * Created by yangtianhang on 15-3-13.
 */
@Repository(value = "oauth2MysqlDao")
public class OAuth2MysqlDaoImpl extends BaseMysqlDao implements OAuth2Dao {
    // oauth2_access_token 表定义
    public static final String OAUTH2_ACCESS_TOKEN_TABLE_NAME = "oauth2_access_token";

    private static final BigintColumn userId = Columns.newUserIdColumn(false, false);
    private static final DatetimeColumn expireAt = Columns.newExpireAtColumn(false);
    private static final TimeStampColumn createAt = Columns.newCreateAtColumn();
    private static final SmallIntColumn appKey = Columns.newAppKeyColumn(false);
    private static final VarcharColumn accessToken =
            (VarcharColumn) new VarcharColumn("access_token", 64).setComment("token").setAllowNull(false);

    public static final String createOauth2AccessTokenDDL = new MysqlDDLBuilder(OAUTH2_ACCESS_TOKEN_TABLE_NAME).
            addColumn(userId).addColumn(expireAt).addColumn(createAt).addColumn(appKey).addColumn(accessToken).
            setPrimaryKey(userId, appKey).buildCreateDDL();

    public void initTable(boolean dropBeforeCreate) {
        super.initTable(createOauth2AccessTokenDDL, BaseMysqlDao.dropDDL(OAUTH2_ACCESS_TOKEN_TABLE_NAME), dropBeforeCreate);
    }

    @Override
    public boolean createAccessToken(long userId, int appKey, String accessToken, long expireAt) {
        return false;
    }

    @Override
    public AccessToken retrieveAccessToken(long userId, int appKey) {
        return null;
    }
}
