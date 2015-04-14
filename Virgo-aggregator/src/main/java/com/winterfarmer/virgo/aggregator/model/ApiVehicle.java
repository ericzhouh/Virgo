package com.winterfarmer.virgo.aggregator.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.winterfarmer.virgo.base.annotation.ApiField;
import com.winterfarmer.virgo.base.annotation.ApiMode;
import com.winterfarmer.virgo.vehicle.model.Vehicle;

/**
 * Created by yangtianhang on 15-4-14.
 */
@ApiMode(desc = "用户车辆信息")
public class ApiVehicle {
    @JSONField(name = "vehicle_id")
    @ApiField(desc = "车辆实体id")
    private long vehicleId;

    @JSONField(name = "user_id")
    @ApiField(desc = "所属用户id")
    private long userId;

    @JSONField(name = "license_plate")
    @ApiField(desc = "车牌号码")
    private String licensePlate;

    @JSONField(name = "vehicle_id_no")
    @ApiField(desc = "车架号")
    private String vehicleIdNo;

    @JSONField(name = "engine_no")
    @ApiField(desc = "发动号")
    private String engineNo;

    @JSONField(name = "state")
    @ApiField(desc = "状态")
    private int state;

    public ApiVehicle(Vehicle vehicle) {
        this.vehicleId = vehicle.getVehicleId();
        this.userId = vehicle.getUserId();
        this.licensePlate = vehicle.getLicensePlate();
        this.vehicleIdNo = vehicle.getVehicleIdNo();
        this.engineNo = vehicle.getEngineNo();
        this.state = vehicle.getState().getIndex();
    }

    public ApiVehicle() {
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

    public String getVehicleIdNo() {
        return vehicleIdNo;
    }

    public void setVehicleIdNo(String vehicleIdNo) {
        this.vehicleIdNo = vehicleIdNo;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
