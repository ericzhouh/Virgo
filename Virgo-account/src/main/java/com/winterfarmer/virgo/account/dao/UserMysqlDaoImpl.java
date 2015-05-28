package com.winterfarmer.virgo.account.dao;

import com.winterfarmer.virgo.account.model.AccountVersion;
import com.winterfarmer.virgo.account.model.User;
import com.winterfarmer.virgo.account.model.UserType;
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
@Repository(value = "userMysqlDao")
public class UserMysqlDaoImpl extends BaseMysqlDao implements UserDao {
    public static final String USER_TABLE_NAME = "user";

    private static final BigintColumn userId = Columns.newUserIdColumn(false, false);
    private static final TimeStampColumn createAt = Columns.newCreateAtColumn();
    private static final VarcharColumn nickName = (VarcharColumn) new VarcharColumn("nick_name", 128).
            setAllowNull(false).setComment("昵称").setUnique(true);
    private static final TinyIntColumn userType = (TinyIntColumn) new TinyIntColumn("user_type").
            setAllowNull(false).setDefaultValue("0").setComment("用户业务类型");
    private static final VarcharColumn salt = (VarcharColumn) new VarcharColumn("salt", 256).
            setAllowNull(false).setComment("盐").setUnique(true);
    private static final VarcharColumn hashedPassword = (VarcharColumn) new VarcharColumn("hashed_password", 256).
            setAllowNull(false).setComment("密码");
    private static final TinyIntColumn version = (TinyIntColumn) new TinyIntColumn("version").
            setAllowNull(false).setDefaultValue("0").setComment("账号系统版本号");
    private static final ExtInfoColumn extInfo = new ExtInfoColumn();

    public static final String createDDL = new MysqlDDLBuilder(USER_TABLE_NAME).
            addColumn(userId).addColumn(createAt).addColumn(nickName).
            addColumn(userType).addColumn(salt).addColumn(hashedPassword).
            addColumn(version).addColumn(extInfo).
            setPrimaryKey(userId).addIndex(nickName).buildCreateDDL();

    public static final RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();

            user.setUserId(userId.getValue(rs));
            user.setNickName(nickName.getValue(rs));
            user.setUserType(UserType.valueByIndex(userType.getValue(rs)));
            user.setCreateAtMs(createAt.getValue(rs));
            user.setSalt(salt.getValue(rs));
            user.setHashedPassword(hashedPassword.getValue(rs));
            user.setVersion(AccountVersion.valueByIndex(version.getValue(rs)));

            return user;
        }
    };

    public void initTable(boolean dropBeforeCreate) {
        super.initTable(createDDL, BaseMysqlDao.dropDDL(USER_TABLE_NAME), dropBeforeCreate);
    }

    private static final String retrieve_user_sql =
            "select * from " + USER_TABLE_NAME + " where " + userId.eqWhich() + ";";

    @Override
    public User retrieveUser(long userId, boolean fromWrite) {
        if (fromWrite) {
            return queryForObject(getWriteJdbcTemplate(), retrieve_user_sql, userRowMapper, userId);
        } else {
            return queryForObject(getReadJdbcTemplate(), retrieve_user_sql, userRowMapper, userId);
        }
    }

    private static final String insert_user_sql =
            insertIntoSQL(USER_TABLE_NAME, userId, nickName, userType, hashedPassword, salt, version, extInfo);

    @Override
    public boolean createUser(long userId, String nickName, UserType userType, String hashedPassword, String salt, AccountVersion version, Map<String, Object> extInfo) {
        return update(insert_user_sql, userId, nickName, userType, hashedPassword, salt, version.getIndex(), ExtInfoColumn.toBytes(extInfo)) > 0;
    }

    private static final String update_user_sql = "update " + USER_TABLE_NAME + " insert " +
            nickName.eqWhich() + ", " +
            userType.eqWhich() + ", " +
            extInfo.eqWhich() +
            " where " + userId.eqWhich() + ";";

    @Override
    public boolean updateUser(User user) {
        ParamChecker.notNull(user, "user");
        return update(update_user_sql,
                user.getNickName(), user.getUserType().getIndex(), ExtInfoColumn.toBytes(user.getProperties()), user.getUserId()) > 0;
    }

    private static final String update_password = updateSql(USER_TABLE_NAME, hashedPassword) +
            where(userId.eqWhich());

    @Override
    public boolean updatePassword(long userId, String password) {
        return update(update_password, password, userId) > 0;
    }

    private static final String retrieve_user_by_nick_name_sql =
            "select * from " + USER_TABLE_NAME + " where " + nickName.eqWhich() + ";";

    @Override
    public User retrieveUserByNickName(String nickName) {
        return queryForObject(getReadJdbcTemplate(), retrieve_user_by_nick_name_sql, userRowMapper, nickName);
    }
}
