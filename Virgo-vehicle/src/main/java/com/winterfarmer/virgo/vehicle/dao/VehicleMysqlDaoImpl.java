package com.winterfarmer.virgo.vehicle.dao;

import com.winterfarmer.virgo.common.definition.CommonState;
import com.winterfarmer.virgo.common.util.ParamChecker;
import com.winterfarmer.virgo.database.BaseDao;
import com.winterfarmer.virgo.database.helper.MysqlDDLBuilder;
import com.winterfarmer.virgo.database.helper.column.Columns;
import com.winterfarmer.virgo.database.helper.column.binary.ExtInfoColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.BigintColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.TinyIntColumn;
import com.winterfarmer.virgo.database.helper.column.string.VarcharColumn;
import com.winterfarmer.virgo.vehicle.model.Vehicle;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by yangtianhang on 15-4-13.
 */
@Repository(value = "vehicleMysqlDao")
public class VehicleMysqlDaoImpl extends BaseDao implements VehicleDao {
    public static final String VEHICLE_TABLE_NAME = "vehicle";

    private static final BigintColumn vehicleId = (BigintColumn) new BigintColumn("vehicle_id").
            setDisplaySize(20).setAutoIncrease(true).setAllowNull(false);
    private static final BigintColumn userId = Columns.newUserIdColumn(false, false);
    private static final VarcharColumn licensePlate = (VarcharColumn) new VarcharColumn("license_plate", 16).
            setAllowNull(true).setComment("车牌号");
    private static final VarcharColumn vehicleIdNo = (VarcharColumn) new VarcharColumn("vehicle_id_no", 32).
            setAllowNull(true).setComment("车架号");
    private static final VarcharColumn engineNo = (VarcharColumn) new VarcharColumn("engine_no", 32).
            setAllowNull(true).setComment("发动机号");
    private static final TinyIntColumn state = Columns.newStateColumn(false);
    private static final ExtInfoColumn extInfo = new ExtInfoColumn();

    public static final String createDDL = new MysqlDDLBuilder(VEHICLE_TABLE_NAME).
            addColumn(vehicleId).addColumn(userId).addColumn(licensePlate).
            addColumn(vehicleIdNo).addColumn(engineNo).addColumn(state).
            addColumn(extInfo).
            setPrimaryKey(vehicleId).addIndex(userId).buildCreateDDL();

    public void initTable(boolean dropBeforeCreate) {
        super.initTable(createDDL, BaseDao.dropDDL(VEHICLE_TABLE_NAME), dropBeforeCreate);
    }

    private static final String insert_vehicle_sql =
            insertIntoSQL(VEHICLE_TABLE_NAME, userId, licensePlate, vehicleIdNo, engineNo, state, extInfo);

    @Override
    public boolean createVehicle(Vehicle vehicle) {
        ParamChecker.notNull(vehicle, "vehicle");
        return update(insert_vehicle_sql, vehicle.getUserId(), vehicle.getLicensePlate(), vehicle.getVehicleIdNo(), vehicle.getEngineNo(),
                vehicle.getState().getIndex(), ExtInfoColumn.toBytes(vehicle.getProperties())) > 0;
    }

    private static final String update_vehicle_sql =
            updateSql(VEHICLE_TABLE_NAME, licensePlate, vehicleIdNo, engineNo, state, extInfo) +
                    new WhereClauseBuilder(vehicleId.eqWhich()).build();

    @Override
    public boolean updateVehicle(Vehicle vehicle) {
        ParamChecker.notNull(vehicle, "vehicle");
        return update(update_vehicle_sql, vehicle.getLicensePlate(), vehicle.getVehicleIdNo(), vehicle.getEngineNo(),
                vehicle.getState().getIndex(), ExtInfoColumn.toBytes(vehicle.getProperties()), vehicle.getVehicleId()) > 0;
    }

    public static final RowMapper<Vehicle> vehicleRowMapper = new RowMapper<Vehicle>() {
        @Override
        public Vehicle mapRow(ResultSet rs, int rowNum) throws SQLException {
            Vehicle vehicle = new Vehicle();
            vehicle.setVehicleId(vehicleId.getValue(rs));
            vehicle.setUserId(userId.getValue(rs));
            vehicle.setLicensePlate(licensePlate.getValue(rs));
            vehicle.setVehicleIdNo(vehicleIdNo.getValue(rs));
            vehicle.setEngineNo(engineNo.getValue(rs));
            vehicle.setState(CommonState.valueByIndex(state.getValue(rs)));
            vehicle.setProperties(extInfo.getValue(rs));
            return vehicle;
        }
    };

    private static final String select_vehicles_by_user_id_sql_depend_on_state =
            selectSql(VEHICLE_TABLE_NAME) + new WhereClauseBuilder(userId.eqWhich()).and(state.eqWhich()).limitOffset();

    private static final String select_vehicles_by_user_id_sql =
            selectSql(VEHICLE_TABLE_NAME) + new WhereClauseBuilder(userId.eqWhich()).and(state.eqWhich()).limitOffset();

    @Override
    public List<Vehicle> retrieveVehicles(long userId, CommonState state, int offset, int limit) {
        if (state != null) {
            return queryForList(getReadJdbcTemplate(), select_vehicles_by_user_id_sql_depend_on_state, vehicleRowMapper, userId, state.getIndex(), limit, offset);
        } else {
            return queryForList(getReadJdbcTemplate(), select_vehicles_by_user_id_sql, vehicleRowMapper, userId, limit, offset);
        }
    }

    private static final String select_vehicles =
            selectSql(VEHICLE_TABLE_NAME) + new WhereClauseBuilder().limitOffset();

    @Override
    public List<Vehicle> retrieveVehicles(int offset, int limit) {
        return queryForList(getReadJdbcTemplate(), select_vehicles, vehicleRowMapper, limit, offset);
    }

    private static final String select_vehicle_by_vehicle_id =
            selectSql(VEHICLE_TABLE_NAME) + new WhereClauseBuilder(vehicleId.eqWhich());

    @Override
    public Vehicle retrieveById(long vehicleId) {
        return queryForObject(getReadJdbcTemplate(), select_vehicle_by_vehicle_id, vehicleRowMapper, vehicleId);
    }
}
