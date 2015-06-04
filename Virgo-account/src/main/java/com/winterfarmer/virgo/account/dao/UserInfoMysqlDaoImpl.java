package com.winterfarmer.virgo.account.dao;

import com.winterfarmer.virgo.account.model.UserInfo;
import com.winterfarmer.virgo.account.model.UserType;
import com.winterfarmer.virgo.database.BaseMysqlDao;
import com.winterfarmer.virgo.database.helper.MysqlDDLBuilder;
import com.winterfarmer.virgo.database.helper.column.Columns;
import com.winterfarmer.virgo.database.helper.column.binary.ExtInfoColumn;
import com.winterfarmer.virgo.database.helper.column.date.DateColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.BigintColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.TinyIntColumn;
import com.winterfarmer.virgo.database.helper.column.string.VarcharColumn;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yangtianhang on 15/6/3.
 */
@Repository(value = "userInfoMysqlDao")
public class UserInfoMysqlDaoImpl extends BaseMysqlDao implements UserInfoDao {
    public static final String USER_INFO_TABLE_NAME = "user_info";

    private static final BigintColumn userId = Columns.newUserIdColumn();
    private static final VarcharColumn nickName = (VarcharColumn) new VarcharColumn("nick_name", 128).
            setAllowNull(false).setComment("昵称").setUnique(false); // 冗余account,可以重复
    private static final VarcharColumn portrait = (VarcharColumn) new VarcharColumn("portrait", 256).
            setAllowNull(true).setComment("portrait");
    private static final TinyIntColumn gender = (TinyIntColumn) new TinyIntColumn("gender").
            setAllowNull(false).setDefaultValue("0").setComment("性别{0,1}->{M,F}");
    private static final DateColumn birthday = (DateColumn) new DateColumn("birthday").
            setAllowNull(false).setDefaultValue("\"1970-01-01\"").setComment("生日,默认1970-01-01");
    private static final VarcharColumn realName = (VarcharColumn) new VarcharColumn("real_name", 16).
            setAllowNull(true).setComment("姓名");
    private static final VarcharColumn email = (VarcharColumn) new VarcharColumn("email", 128).
            setAllowNull(true).setComment("email");
    private static final VarcharColumn introduction = (VarcharColumn) new VarcharColumn("introduction", 512).
            setAllowNull(true).setComment("introduction");
    private static final TinyIntColumn userType = (TinyIntColumn) new TinyIntColumn("user_type").
            setAllowNull(false).setDefaultValue("0").setComment("用户类型");
    private static final ExtInfoColumn extInfo = new ExtInfoColumn();

    public static final String createDDL = new MysqlDDLBuilder(USER_INFO_TABLE_NAME).
            addColumn(userId).addColumn(nickName).addColumn(portrait).
            addColumn(gender).addColumn(birthday).addColumn(realName).
            addColumn(email).addColumn(introduction).addColumn(userType).
            addColumn(extInfo).
            setPrimaryKey(userId).buildCreateDDL();

    public static final RowMapper<UserInfo> userInfoRowMapper = new RowMapper<UserInfo>() {
        @Override
        public UserInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserInfo userInfo = new UserInfo();

            userInfo.setUserId(userId.getValue(rs));
            userInfo.setNickName(nickName.getValue(rs));
            userInfo.setPortrait(portrait.getValue(rs));
            userInfo.setGender(gender.getValue(rs));
            userInfo.setBirthday(birthday.getValue(rs));
            userInfo.setRealName(realName.getValue(rs));
            userInfo.setEmail(email.getValue(rs));
            userInfo.setIntroduction(introduction.getValue(rs));
            userInfo.setUserType(UserType.valueByIndex(userType.getValue(rs)));
            userInfo.setProperties(extInfo.getValue(rs));

            return userInfo;
        }
    };

    public void initTable(boolean dropBeforeCreate) {
        super.initTable(createDDL, BaseMysqlDao.dropDDL(USER_INFO_TABLE_NAME), dropBeforeCreate);
    }

    private static final String insert_user_info_sql = insertIntoSQL(USER_INFO_TABLE_NAME,
            userId, nickName, portrait,
            gender, birthday, realName,
            email, introduction, userType,
            extInfo);

    @Override
    public boolean create(UserInfo userInfo) {
        return update(insert_user_info_sql,
                userInfo.getUserId(), userInfo.getNickName(), userInfo.getPortrait(),
                userInfo.getGender(), userInfo.getBirthday(), userInfo.getRealName(),
                userInfo.getEmail(), userInfo.getIntroduction(), userInfo.getUserType().getIndex(),
                ExtInfoColumn.toBytes(userInfo.getProperties())) > 0;
    }

    private static final String update_user_info_sql = updateSql(USER_INFO_TABLE_NAME,
            nickName, portrait, gender,
            birthday, realName, email,
            introduction, userType, extInfo) +
            where(userId.eqWhich());

    @Override
    public boolean update(UserInfo userInfo) {
        return update(update_user_info_sql,
                userInfo.getNickName(), userInfo.getPortrait(), userInfo.getGender(),
                userInfo.getBirthday(), userInfo.getRealName(), userInfo.getEmail(),
                userInfo.getIntroduction(), userInfo.getUserType().getIndex(), ExtInfoColumn.toBytes(userInfo.getProperties()),
                userInfo.getUserId()) > 0;
    }

    private static final String select_user_info_sql = selectAllSql(USER_INFO_TABLE_NAME) +
            where(userId.eqWhich());

    @Override
    public UserInfo retrieveUser(long userId, boolean fromWrite) {
        if (fromWrite) {
            return queryForObject(getWriteJdbcTemplate(), select_user_info_sql, userInfoRowMapper, userId);
        } else {
            return queryForObject(getReadJdbcTemplate(), select_user_info_sql, userInfoRowMapper, userId);
        }
    }
}
