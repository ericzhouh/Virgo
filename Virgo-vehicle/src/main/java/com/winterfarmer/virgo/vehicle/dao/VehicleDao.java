package com.winterfarmer.virgo.vehicle.dao;

import com.winterfarmer.virgo.vehicle.model.Vehicle;

import java.util.List;

/**
 * Created by yangtianhang on 15-4-13.
 */
public interface VehicleDao {
    boolean createVehicle(Vehicle vehicle);

    boolean updateVehicle(Vehicle vehicle);

    List<Vehicle> retrieveVehicles(long userId);
}
