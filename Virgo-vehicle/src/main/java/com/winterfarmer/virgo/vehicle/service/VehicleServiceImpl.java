package com.winterfarmer.virgo.vehicle.service;

import com.winterfarmer.virgo.common.definition.CommonState;
import com.winterfarmer.virgo.vehicle.dao.VehicleDao;
import com.winterfarmer.virgo.vehicle.model.Vehicle;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by yangtianhang on 15-4-14.
 */
@Service("vehicleService")
public class VehicleServiceImpl implements VehicleService {
    @Resource(name = "vehicleMysqlDao")
    VehicleDao vehicleDao;

    @Override
    public Vehicle createVehicle(Vehicle vehicle) {
        long vehicleId = vehicleDao.createVehicle(vehicle);
        if (vehicleId <= 0) {
            return null;
        }

        return vehicleDao.retrieveById(vehicleId);
    }

    @Override
    public boolean updateVehicle(Vehicle vehicle) {
        return vehicleDao.updateVehicle(vehicle);
    }

    @Override
    public List<Vehicle> getVehicles(long userId, CommonState state, int page, int count) {
        return vehicleDao.retrieveVehicles(userId, state, page * count, count);
    }

    @Override
    public List<Vehicle> getVehicles(int page, int count) {
        return vehicleDao.retrieveVehicles(page * count, count);
    }

    @Override
    public Vehicle getVehicle(long vehicleId) {
        return vehicleDao.retrieveById(vehicleId);
    }
}
