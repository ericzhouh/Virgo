package com.winterfarmer.virgo.vehicle.dao;

import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.vehicle.model.Vehicle;

import java.util.List;

/**
 * Created by yangtianhang on 15-4-13.
 */
public interface VehicleDao {
    long createVehicle(Vehicle vehicle);

    boolean updateVehicle(Vehicle vehicle);

    List<Vehicle> retrieveVehicles(long userId, CommonState state, int limit, int offset);

    List<Vehicle> retrieveVehicles(int limit, int offset);

    Vehicle retrieveById(long vehicleId);
}
