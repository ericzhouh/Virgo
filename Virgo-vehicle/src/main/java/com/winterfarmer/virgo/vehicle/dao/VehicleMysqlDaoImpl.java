package com.winterfarmer.virgo.vehicle.dao;

import com.winterfarmer.virgo.database.BaseDao;
import com.winterfarmer.virgo.database.helper.column.Columns;
import com.winterfarmer.virgo.database.helper.column.numeric.BigintColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.TinyIntColumn;
import com.winterfarmer.virgo.database.helper.column.string.VarcharColumn;
import com.winterfarmer.virgo.vehicle.model.Vehicle;

import java.util.List;

/**
 * Created by yangtianhang on 15-4-13.
 */
public class VehicleMysqlDaoImpl extends BaseDao implements VehicleDao {
    public static final String VEHICLE_TABLE_NAME = "vehicle";

    private static final BigintColumn vehicleId = (BigintColumn) new BigintColumn("vehicle_id").
            setDisplaySize(20).setAutoIncrease(true).setAllowNull(false);
    private static final BigintColumn userId = Columns.newUserIdColumn(false, false);
    private static final VarcharColumn licensePlate = (VarcharColumn) new VarcharColumn("license_plate", 16).
            setAllowNull(true).setComment("车牌号");
    private static final VarcharColumn vehicleIdNo = (VarcharColumn) new VarcharColumn("vehicle_id_no", 32).
            setAllowNull(true).setComment("车架号");
    private static final VarcharColumn engineNo = (VarcharColumn) new VarcharColumn("license_plate", 32).
            setAllowNull(true).setComment("发动机号");
    private static final TinyIntColumn state = Columns.newStateColumn(false);

    @Override
    public boolean createVehicle(Vehicle vehicle) {
        return false;
    }

    @Override
    public boolean updateVehicle(Vehicle vehicle) {
        return false;
    }

    @Override
    public List<Vehicle> retrieveVehicles(long userId) {
        return null;
    }
}
