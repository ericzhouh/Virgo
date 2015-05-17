package com.winterfarmer.virgo.storage.id;

import com.winterfarmer.virgo.storage.id.dao.IdModelMysqlDao;
import org.springframework.stereotype.Repository;

/**
 * Created by yangtianhang on 15/5/14.
 */
@Repository(value = "toyIdModelMysqlDao")
public class ToyIdModelMysqlDao {
//    public static final String TOY_TABLE_NAME = "toy";
//
//    private static final BigintColumn id = Columns.newIdColumn("toy", true, false);
//    private static final VarcharColumn name = (VarcharColumn) new VarcharColumn("name", 128).setAllowNull(false);
//
//    public static final String createDDL = new MysqlDDLBuilder(TOY_TABLE_NAME).
//            addColumn(id).addColumn(name).setPrimaryKey(id).buildCreateDDL();
//
//    public static final RowMapper<IdModelRedisBiz.ToyIdModel> toyRowMapper = new RowMapper<IdModelRedisBiz.ToyIdModel>() {
//        @Override
//        public IdModelRedisBiz.ToyIdModel mapRow(ResultSet rs, int rowNum) throws SQLException {
//            IdModelRedisBiz.ToyIdModel toy = new IdModelRedisBiz.ToyIdModel();
//            toy.setId(id.getValue(rs));
//            toy.setName(name.getValue(rs));
//            return toy;
//        }
//    };
//
//    public ToyIdModelMysqlDao() {
//        super(TOY_TABLE_NAME, id, toyRowMapper);
//    }
//
//    public void initTable(boolean dropBeforeCreate) {
//        super.initTable(createDDL, dropDDL(TOY_TABLE_NAME), dropBeforeCreate);
//    }
//
//    private static final String insert_toy_sql = insertIntoSQL(TOY_TABLE_NAME, id, name);
//
//    private static final String update_toy_sql = updateSql(TOY_TABLE_NAME, name)
//            + new WhereClauseBuilder(id.eqWhich()).build();
//
//    @Override
//    protected PreparedStatement createInsertPreparedStatement(Connection connection, IdModelRedisBiz.ToyIdModel object) throws SQLException {
//        return null;
//    }
//
//    @Override
//    protected int doUpdate(IdModelRedisBiz.ToyIdModel object) {
//        return 0;
//    }
}
