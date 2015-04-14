package com.winterfarmer.virgo.vehicle.service;

import com.winterfarmer.virgo.common.definition.CommonState;
import com.winterfarmer.virgo.vehicle.model.Vehicle;

import java.util.List;

/**
 * Created by yangtianhang on 15-4-14.
 */
public interface VehicleService {
    Vehicle createVehicle(Vehicle vehicle);

    boolean updateVehicle(Vehicle vehicle);

    List<Vehicle> getVehicles(long userId, CommonState state, int page, int count);

    List<Vehicle> getVehicles(int page, int count);

    Vehicle getVehicle(long vehicleId);
}
