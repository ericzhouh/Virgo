package com.winterfarmer.virgo.account.dao;

import com.winterfarmer.virgo.account.model.GroupType;
import com.winterfarmer.virgo.account.model.Privilege;
import com.winterfarmer.virgo.database.BaseDao;
import com.winterfarmer.virgo.database.helper.MysqlDDLBuilder;
import com.winterfarmer.virgo.database.helper.column.Columns;
import com.winterfarmer.virgo.database.helper.column.numeric.BigintColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.SmallIntColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.TinyIntColumn;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by yangtianhang on 15-4-17.
 */
@Repository(value = "privilegeMysqlDao")
public class PrivilegeMysqlDaoImpl extends BaseDao implements PrivilegeDao {
    public static final String PRIVILEGE_TABLE_NAME = "privilege";

    private static final BigintColumn userId = Columns.newUserIdColumn(true, false);
    private static final SmallIntColumn group = (SmallIntColumn) new SmallIntColumn("group").
            setAllowNull(false).setDefaultValue("0").setComment("组");
    private static final TinyIntColumn privileges = (TinyIntColumn) new TinyIntColumn("privileges").
            setAllowNull(false).setDefaultValue("0").setComment("权限位");

    public static final String createDDL = new MysqlDDLBuilder(PRIVILEGE_TABLE_NAME).
            addColumn(userId).addColumn(group).addColumn(privileges).
            setPrimaryKey(userId, group).buildCreateDDL();

    public void initTable(boolean dropBeforeCreate) {
        super.initTable(createDDL, BaseDao.dropDDL(PRIVILEGE_TABLE_NAME), dropBeforeCreate);
    }

    private static final String insert_privilege_sql =
            insertIntoSQL(PRIVILEGE_TABLE_NAME, userId, group, privileges);

    @Override
    public boolean createPrivilege(long userId, GroupType groupType, int privileges) {
        return update(insert_privilege_sql, userId, groupType.getIndex(), privileges) > 0;
    }

    private static final String update_privilege_sql =
            updateSql(PRIVILEGE_TABLE_NAME, privileges) + where(userId.eqWhich()).and(group.eqWhich()).build();

    @Override
    public boolean updatePrivilege(long userId, GroupType groupType, int privileges) {
        return update(update_privilege_sql, privileges, userId, groupType.getIndex()) > 0;
    }

    public static final RowMapper<Privilege> privilegeRowMapper = new RowMapper<Privilege>() {
        @Override
        public Privilege mapRow(ResultSet rs, int rowNum) throws SQLException {
            Privilege privilege = new Privilege();
            privilege.setUserId(userId.getValue(rs));
            privilege.setGroupType(GroupType.valueByIndex(group.getValue(rs)));
            privilege.setPrivileges(privileges.getValue(rs));

            return privilege;
        }
    };

    private static final String select_privileges_by_user_id = selectAllSql(PRIVILEGE_TABLE_NAME) +
            where(userId.eqWhich()).build();

    @Override
    public List<Privilege> retrievePrivileges(long userId) {
        return queryForList(getReadJdbcTemplate(), select_privileges_by_user_id, privilegeRowMapper, userId);
    }

    private static final String select_privilege = selectAllSql(PRIVILEGE_TABLE_NAME) +
            where(userId.eqWhich()).and(group.eqWhich()).build();

    @Override
    public Privilege retrievePrivilege(long userId, GroupType groupType) {
        return queryForObject(getReadJdbcTemplate(), select_privilege, privilegeRowMapper, userId, groupType.getIndex());
    }
}
