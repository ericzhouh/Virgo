package com.winterfarmer.virgo.vehicle.model;

import com.winterfarmer.virgo.base.model.BaseModel;
import com.winterfarmer.virgo.base.model.CommonState;

/**
 * Created by yangtianhang on 15-4-13.
 */
public class Vehicle extends BaseModel {
    private static final long serialVersionUID = 5728119100823766751L;

    private long vehicleId;
    private long userId;
    private String licensePlate;
    private String vehicleIdNo;
    private String engineNo;
    private CommonState state;

    public Vehicle() {
    }

    public CommonState getState() {
        return state;
    }

    public void setState(CommonState state) {
        this.state = state;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public String getVehicleIdNo() {
        return vehicleIdNo;
    }

    public void setVehicleIdNo(String vehicleIdNo) {
        this.vehicleIdNo = vehicleIdNo;
    }

    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
}
