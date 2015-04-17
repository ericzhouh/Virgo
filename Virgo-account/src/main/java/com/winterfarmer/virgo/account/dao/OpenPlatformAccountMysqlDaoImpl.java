package com.winterfarmer.virgo.account.dao;

import com.winterfarmer.virgo.account.model.OpenPlatformAccount;
import com.winterfarmer.virgo.account.model.PlatformType;
import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.database.BaseDao;
import com.winterfarmer.virgo.database.helper.MysqlDDLBuilder;
import com.winterfarmer.virgo.database.helper.column.Columns;
import com.winterfarmer.virgo.database.helper.column.binary.ExtInfoColumn;
import com.winterfarmer.virgo.database.helper.column.date.TimeStampColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.BigintColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.TinyIntColumn;
import com.winterfarmer.virgo.database.helper.column.string.VarcharColumn;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yangtianhang on 15-3-26.
 */
@Repository(value = "openPlatformAccountMysqlDao")
public class OpenPlatformAccountMysqlDaoImpl extends BaseDao implements OpenPlatformAccountDao {
    public static final String OPEN_PLATFORM_ACCOUNT_TABLE_NAME = "open_platform_account";

    private static final VarcharColumn openId = (VarcharColumn) new VarcharColumn("open_id", 128).setAllowNull(false).setComment("第三方id");
    private static final TinyIntColumn platform = (TinyIntColumn) new TinyIntColumn("platform").setAllowNull(false).setComment("平台id");
    private static final BigintColumn userId = Columns.newUserIdColumn(false, false);
    private static final VarcharColumn openToken = (VarcharColumn) new VarcharColumn("open_token", 128).setAllowNull(false).setComment("第三方平台token");
    private static final TinyIntColumn state = Columns.newStateColumn(false);
    private static final TimeStampColumn createAt = Columns.newCreateAtColumn();
    private static final ExtInfoColumn extInfo = new ExtInfoColumn();

    public static final String createDDL = new MysqlDDLBuilder(OPEN_PLATFORM_ACCOUNT_TABLE_NAME).
            addColumn(userId).addColumn(platform).addColumn(openId).
            addColumn(openToken).addColumn(state).addColumn(createAt).
            addColumn(extInfo).
            setPrimaryKey(openId, platform).buildCreateDDL();

    public void initTable(boolean dropBeforeCreate) {
        super.initTable(createDDL, BaseDao.dropDDL(OPEN_PLATFORM_ACCOUNT_TABLE_NAME), dropBeforeCreate);
    }

    public static final RowMapper<OpenPlatformAccount> openPlatformAccountRowMapper = new RowMapper<OpenPlatformAccount>() {
        @Override
        public OpenPlatformAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
            OpenPlatformAccount openPlatformAccount = new OpenPlatformAccount();
            openPlatformAccount.setUserId(userId.getValue(rs));
            openPlatformAccount.setPlatformType(PlatformType.valueByIndex(platform.getValue(rs)));
            openPlatformAccount.setOpenId(openId.getValue(rs));
            openPlatformAccount.setOpenToken(openToken.getValue(rs));
            openPlatformAccount.setCommonState(CommonState.valueByIndex(state.getValue(rs)));
            openPlatformAccount.setCreateAtMs(createAt.getValue(rs));
            openPlatformAccount.setProperties(extInfo.getValue(rs));
            return openPlatformAccount;
        }
    };

    private static final String insert_open_platform_account_sql =
            insertIntoSQL(OPEN_PLATFORM_ACCOUNT_TABLE_NAME, userId, platform, openId, openToken, state, extInfo);

    @Override
    public boolean createOpenPlatformAccount(OpenPlatformAccount openPlatformAccount) {
        return update(insert_open_platform_account_sql,
                openPlatformAccount.getUserId(), openPlatformAccount.getPlatformType().getIndex(), openPlatformAccount.getOpenId(),
                openPlatformAccount.getOpenToken(), openPlatformAccount.getCommonState().getIndex(), ExtInfoColumn.toBytes(openPlatformAccount.getProperties())) > 0;
    }

    private static final String retrieve_open_platform_account_sql =
            selectAllSql(OPEN_PLATFORM_ACCOUNT_TABLE_NAME) + new WhereClauseBuilder(openId.eqWhich()).and(platform.eqWhich());

    @Override
    public OpenPlatformAccount retrieveOpenPlatformAccount(String openId, PlatformType platformType) {
        return queryForObject(getReadJdbcTemplate(), retrieve_open_platform_account_sql, openPlatformAccountRowMapper, openId, platformType.getIndex());
    }

    //    @Override
//    public OpenPlatformAccount retrieveOpenPlatformAccount(long userId, String openId) {
//        return queryForObject(getReadJdbcTemplate(), retrieve_open_platform_account_sql, openPlatformAccountRowMapper, userId, openId);
//    }
}
