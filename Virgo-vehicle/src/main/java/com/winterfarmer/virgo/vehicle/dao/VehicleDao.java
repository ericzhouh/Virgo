package com.winterfarmer.virgo.vehicle.dao;

import com.winterfarmer.virgo.common.definition.CommonState;
import com.winterfarmer.virgo.vehicle.model.Vehicle;

import java.util.List;

/**
 * Created by yangtianhang on 15-4-13.
 */
public interface VehicleDao {
    long createVehicle(Vehicle vehicle);

    boolean updateVehicle(Vehicle vehicle);

    List<Vehicle> retrieveVehicles(long userId, CommonState state, int offset, int limit);

    List<Vehicle> retrieveVehicles(int offset, int limit);

    Vehicle retrieveById(long vehicleId);
}
