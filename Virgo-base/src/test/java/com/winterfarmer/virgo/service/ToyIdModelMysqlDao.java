package com.winterfarmer.virgo.service;

import com.winterfarmer.virgo.base.bizconfig.IdModelRedisBiz;
import com.winterfarmer.virgo.base.dao.IdModelMysqlDao;
import com.winterfarmer.virgo.database.BaseMysqlDao;
import com.winterfarmer.virgo.database.helper.MysqlDDLBuilder;
import com.winterfarmer.virgo.database.helper.column.Columns;
import com.winterfarmer.virgo.database.helper.column.numeric.BigintColumn;
import com.winterfarmer.virgo.database.helper.column.string.VarcharColumn;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yangtianhang on 15/5/14.
 */
@Repository(value = "toyIdModelMysqlDao")
public class ToyIdModelMysqlDao extends IdModelMysqlDao<IdModelRedisBiz.ToyIdModel> {
    public static final String TOY_TABLE_NAME = "toy";

    private static final BigintColumn id = Columns.newIdColumn("toy", true, false);
    private static final VarcharColumn name = (VarcharColumn) new VarcharColumn("name", 128).setAllowNull(false);

    public static final String createDDL = new MysqlDDLBuilder(TOY_TABLE_NAME).
            addColumn(id).addColumn(name).setPrimaryKey(id).buildCreateDDL();

    public static final RowMapper<IdModelRedisBiz.ToyIdModel> toyRowMapper = new RowMapper<IdModelRedisBiz.ToyIdModel>() {
        @Override
        public IdModelRedisBiz.ToyIdModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            IdModelRedisBiz.ToyIdModel toy = new IdModelRedisBiz.ToyIdModel();
            toy.setId(id.getValue(rs));
            toy.setName(name.getValue(rs));
            return toy;
        }
    };

    public ToyIdModelMysqlDao() {
        super(TOY_TABLE_NAME, id, toyRowMapper);
    }

    public void initTable(boolean dropBeforeCreate) {
        super.initTable(createDDL, BaseMysqlDao.dropDDL(TOY_TABLE_NAME), dropBeforeCreate);
    }

    private static final String insert_toy_sql = insertIntoSQL(TOY_TABLE_NAME, id, name);

    @Override
    public boolean insert(IdModelRedisBiz.ToyIdModel object) {
        return update(insert_toy_sql, object.getId(), object.getName()) > 0;
    }

    private static final String update_toy_sql = updateSql(TOY_TABLE_NAME, name)
            + new WhereClauseBuilder(id.eqWhich()).build();

    @Override
    public boolean update(IdModelRedisBiz.ToyIdModel object) {
        return update(update_toy_sql, object.getName(), object.getId()) > 0;
    }
}
