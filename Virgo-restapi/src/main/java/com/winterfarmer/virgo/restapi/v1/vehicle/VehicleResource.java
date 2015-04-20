package com.winterfarmer.virgo.restapi.v1.vehicle;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.winterfarmer.virgo.aggregator.model.ApiVehicle;
import com.winterfarmer.virgo.base.model.CommonState;
import com.winterfarmer.virgo.restapi.BaseResource;
import com.winterfarmer.virgo.restapi.core.annotation.ParamSpec;
import com.winterfarmer.virgo.restapi.core.annotation.RestApiInfo;
import com.winterfarmer.virgo.restapi.core.exception.RestExceptionFactor;
import com.winterfarmer.virgo.restapi.core.exception.VirgoRestException;
import com.winterfarmer.virgo.vehicle.model.Vehicle;
import com.winterfarmer.virgo.vehicle.service.VehicleService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by yangtianhang on 15-4-14.
 */
@Path("vehicle")
@Component("vehicleResource")
public class VehicleResource extends BaseResource {
    protected static final String LICENSE_PLATE_SPEC = "string:6~24";
    protected static final String VEHICLE_ID_NO_SPEC = "string:6~32";
    protected static final String ENGINE_NO_SPEC = "string:6~32";

    @Resource(name = "vehicleService")
    VehicleService vehicleService;

    private static final Function<Vehicle, ApiVehicle> transVehicleApiFunc = new Function<Vehicle, ApiVehicle>() {
        @Override
        public ApiVehicle apply(Vehicle vehicle) {
            return new ApiVehicle((vehicle));
        }
    };

    @Path("vehicle.json")
    @GET
    @RestApiInfo(
            desc = "获取用户车辆信息",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            errors = {RestExceptionFactor.VEHICLE_NOT_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ApiVehicle getUserVehicle(
            @QueryParam("vehicle_id")
            @ParamSpec(isRequired = true, spec = NORMAL_LONG_ID_SPEC, desc = "用户汽车id")
            long vehicleId,
            @HeaderParam(HEADER_USER_ID)
            long userId) {
        Vehicle vehicle = vehicleService.getVehicle(vehicleId);
        if (vehicle == null || vehicle.getUserId() != userId) {
            throw new VirgoRestException(RestExceptionFactor.VEHICLE_NOT_EXISTED);
        }

        return new ApiVehicle(vehicle);
    }

    @Path("vehicle_list.json")
    @GET
    @RestApiInfo(
            desc = "获取用户车辆信息列表",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            errors = {}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApiVehicle> getUserVehicleList(
            @QueryParam("page")
            @ParamSpec(isRequired = false, spec = NORMAL_PAGE_SPEC, desc = NORMAL_PAGE_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_NUM)
            int page,
            @QueryParam("count")
            @ParamSpec(isRequired = false, spec = NORMAL_COUNT_SPEC, desc = NORMAL_COUNT_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_COUNT)
            int count,
            @HeaderParam(HEADER_USER_ID)
            long userId) {
        List<Vehicle> vehicle = vehicleService.getVehicles(userId, CommonState.NORMAL, page, count);
        return Lists.transform(vehicle, new Function<Vehicle, ApiVehicle>() {
            @Override
            public ApiVehicle apply(Vehicle vehicle) {
                return new ApiVehicle((vehicle));
            }
        });
    }

    @Path("new_vehicle.json")
    @POST
    @RestApiInfo(
            desc = "用户添加车辆信息",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            errors = {RestExceptionFactor.VEHICLE_NOT_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ApiVehicle createUserVehicle(
            @FormParam("license_plate")
            @ParamSpec(isRequired = true, spec = LICENSE_PLATE_SPEC, desc = "车牌号")
            String licensePlate,
            @FormParam("vehicle_id_no")
            @ParamSpec(isRequired = false, spec = VEHICLE_ID_NO_SPEC, desc = "车架号")
            String vehicleIdNo,
            @FormParam("engine_no")
            @ParamSpec(isRequired = false, spec = ENGINE_NO_SPEC, desc = "发动机号")
            String engineNo,
            @FormParam("extension")
            @ParamSpec(isRequired = false, spec = EXTENSION_SPEC, desc = "扩展信息")
            String extension,
            @HeaderParam(HEADER_USER_ID)
            long userId) {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(licensePlate);
        vehicle.setVehicleIdNo(vehicleIdNo);
        vehicle.setEngineNo(engineNo);
        if (extension != null) {
            vehicle.setProperties(JSON.parseObject(extension));
        }
        vehicle.setUserId(userId);
        vehicle.setState(CommonState.NORMAL);

        Vehicle newVehicle = vehicleService.createVehicle(vehicle);
        if (newVehicle == null) {
            throw new VirgoRestException(RestExceptionFactor.INTERNAL_SERVER_ERROR);
        } else {
            return transVehicleApiFunc.apply(newVehicle);
        }
    }

    @Path("vehicle.json")
    @POST
    @RestApiInfo(
            desc = "用户修改车辆信息",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            errors = {RestExceptionFactor.VEHICLE_NOT_EXISTED}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ApiVehicle updateUserVehicle(
            @FormParam("vehicle_id")
            @ParamSpec(isRequired = true, spec = NORMAL_LONG_ID_SPEC, desc = "用户汽车id")
            long vehicleId,
            @FormParam("license_plate")
            @ParamSpec(isRequired = true, spec = LICENSE_PLATE_SPEC, desc = "车牌号")
            String licensePlate,
            @FormParam("state")
            @ParamSpec(isRequired = true, spec = COMMON_STATE_SPEC, desc = "删除 or 正常")
            CommonState state,
            @FormParam("vehicle_id_no")
            @ParamSpec(isRequired = false, spec = VEHICLE_ID_NO_SPEC, desc = "车架号")
            String vehicleIdNo,
            @FormParam("engine_no")
            @ParamSpec(isRequired = false, spec = ENGINE_NO_SPEC, desc = "发动机号")
            String engineNo,
            @FormParam("extension")
            @ParamSpec(isRequired = false, spec = EXTENSION_SPEC, desc = "扩展信息")
            String extension,
            @HeaderParam(HEADER_USER_ID)
            long userId) {
        Vehicle vehicle = vehicleService.getVehicle(vehicleId);
        if (vehicle == null || vehicle.getUserId() != userId) {
            throw new VirgoRestException(RestExceptionFactor.VEHICLE_NOT_EXISTED);
        }

        vehicle.setLicensePlate(licensePlate);
        vehicle.setState(state);

        if (vehicleIdNo != null) {
            vehicle.setVehicleIdNo(vehicleIdNo);
        }
        if (engineNo != null) {
            vehicle.setEngineNo(engineNo);
        }
        if (extension != null) {
            setProperties(vehicle, extension);
        }

        boolean result = vehicleService.updateVehicle(vehicle);
        if (result) {
            return transVehicleApiFunc.apply(vehicle);
        } else {
            throw new VirgoRestException(RestExceptionFactor.INTERNAL_SERVER_ERROR);
        }
    }
}
