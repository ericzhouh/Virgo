package com.winterfarmer.virgo.account.dao;

import com.winterfarmer.virgo.account.model.Account;
import com.winterfarmer.virgo.account.model.AccountVersion;
import com.winterfarmer.virgo.common.util.ParamChecker;
import com.winterfarmer.virgo.database.BaseMysqlDao;
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
import java.util.Map;

/**
 * Created by yangtianhang on 15-3-5.
 */
@Repository(value = "accountMysqlDao")
public class AccountMysqlDaoImpl extends BaseMysqlDao implements AccountDao {
    public static final String ACCOUNT_TABLE_NAME = "account";

    private static final BigintColumn userId = Columns.newUserIdColumn();
    private static final TimeStampColumn createAt = Columns.newCreateAtColumn();
    private static final VarcharColumn nickName = (VarcharColumn) new VarcharColumn("nick_name", 128).
            setAllowNull(false).setComment("昵称").setUnique(true);
    private static final VarcharColumn salt = (VarcharColumn) new VarcharColumn("salt", 256).
            setAllowNull(false).setComment("盐").setUnique(true);
    private static final VarcharColumn hashedPassword = (VarcharColumn) new VarcharColumn("hashed_password", 256).
            setAllowNull(false).setComment("密码");
    private static final TinyIntColumn version = (TinyIntColumn) new TinyIntColumn("version").
            setAllowNull(false).setDefaultValue("0").setComment("账号系统版本号");
    private static final ExtInfoColumn extInfo = new ExtInfoColumn();

    public static final String createDDL = new MysqlDDLBuilder(ACCOUNT_TABLE_NAME).
            addColumn(userId).addColumn(createAt).addColumn(nickName).
            addColumn(salt).addColumn(hashedPassword).
            addColumn(version).addColumn(extInfo).
            setPrimaryKey(userId).addIndex(nickName).buildCreateDDL();

    public static final RowMapper<Account> accountRowMapper = new RowMapper<Account>() {
        @Override
        public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
            Account account = new Account();

            account.setUserId(userId.getValue(rs));
            account.setNickName(nickName.getValue(rs));
            account.setCreateAtMs(createAt.getValue(rs));
            account.setSalt(salt.getValue(rs));
            account.setHashedPassword(hashedPassword.getValue(rs));
            account.setVersion(AccountVersion.valueByIndex(version.getValue(rs)));

            return account;
        }
    };

    public void initTable(boolean dropBeforeCreate) {
        super.initTable(createDDL, BaseMysqlDao.dropDDL(ACCOUNT_TABLE_NAME), dropBeforeCreate);
    }

    private static final String retrieve_user_sql =
            "select * from " + ACCOUNT_TABLE_NAME + " where " + userId.eqWhich() + ";";

    @Override
    public Account retrieveAccount(long userId, boolean fromWrite) {
        if (fromWrite) {
            return queryForObject(getWriteJdbcTemplate(), retrieve_user_sql, accountRowMapper, userId);
        } else {
            return queryForObject(getReadJdbcTemplate(), retrieve_user_sql, accountRowMapper, userId);
        }
    }

    private static final String insert_account_sql =
            insertIntoSQL(ACCOUNT_TABLE_NAME, userId, nickName, hashedPassword, salt, version, extInfo);

    @Override
    public boolean createAccount(long userId, String nickName, String hashedPassword, String salt, AccountVersion version, Map<String, Object> extInfo) {
        return update(insert_account_sql, userId, nickName, hashedPassword, salt, version.getIndex(), ExtInfoColumn.toBytes(extInfo)) > 0;
    }

    private static final String update_account_sql = "updateApplyingExpert " + ACCOUNT_TABLE_NAME + " set " +
            nickName.eqWhich() + ", " +
            extInfo.eqWhich() +
            " where " + userId.eqWhich() + ";";

    @Override
    public boolean updateAccount(Account account) {
        ParamChecker.notNull(account, "user");
        return update(update_account_sql,
                account.getNickName(), ExtInfoColumn.toBytes(account.getProperties()), account.getUserId()) > 0;
    }

    private static final String update_password = updateSql(ACCOUNT_TABLE_NAME, hashedPassword) +
            where(userId.eqWhich());

    @Override
    public boolean updatePassword(long userId, String password) {
        return update(update_password, password, userId) > 0;
    }

    private static final String retrieve_account_by_nick_name_sql =
            "select * from " + ACCOUNT_TABLE_NAME + " where " + nickName.eqWhich() + ";";

    @Override
    public Account retrieveAccountByNickName(String nickName) {
        return queryForObject(getReadJdbcTemplate(), retrieve_account_by_nick_name_sql, accountRowMapper, nickName);
    }
}