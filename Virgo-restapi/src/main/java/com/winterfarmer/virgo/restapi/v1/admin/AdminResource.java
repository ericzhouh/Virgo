package com.winterfarmer.virgo.restapi.v1.admin;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.winterfarmer.virgo.account.model.*;
import com.winterfarmer.virgo.account.service.AccountService;
import com.winterfarmer.virgo.account.service.StaffService;
import com.winterfarmer.virgo.aggregator.model.ApiPrivilege;
import com.winterfarmer.virgo.aggregator.model.ApiStaff;
import com.winterfarmer.virgo.aggregator.model.ApiUser;
import com.winterfarmer.virgo.common.util.CollectionsUtil;
import com.winterfarmer.virgo.restapi.BaseResource;
import com.winterfarmer.virgo.restapi.core.annotation.ParamSpec;
import com.winterfarmer.virgo.restapi.core.annotation.ResourceOverview;
import com.winterfarmer.virgo.restapi.core.annotation.RestApiInfo;
import com.winterfarmer.virgo.restapi.core.exception.RestExceptionFactor;
import com.winterfarmer.virgo.restapi.core.exception.VirgoRestException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * Created by yangtianhang on 15-4-16.
 */
@Path("admin")
@ResourceOverview(desc = "管理员接口")
@Component("adminResource")
public class AdminResource extends BaseResource {
    @Resource(name = "staffService")
    StaffService staffService;

    @Resource(name = "accountService")
    AccountService accountService;

    // list员工
    @Path("staff_list.json")
    @GET
    @RestApiInfo(
            desc = "列出员工",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            groupType = GroupType.PUBLIC,
            rolePrivileges = {RolePrivilege.VIEW},
            resultDemo = ApiStaff.class,
            errors = {}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApiStaff> getStaff(
            @QueryParam("group")
            @ParamSpec(isRequired = false, spec = ENUM_COMPLETE_SPEC, desc = "权限组")
            GroupType groupType,
            @QueryParam(PAGE_PARAM_NAME)
            @ParamSpec(isRequired = false, spec = NORMAL_PAGE_SPEC, desc = NORMAL_PAGE_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_NUM)
            int page,
            @QueryParam(COUNT_PARAM_NAME)
            @ParamSpec(isRequired = false, spec = NORMAL_COUNT_SPEC, desc = NORMAL_COUNT_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_COUNT)
            int count) {
        List<Privilege> privileges = staffService.listStaffPrivilege(groupType, page, count);
        Map<Long, List<Privilege>> privilegeMap = Maps.newHashMap();
        for (Privilege privilege : privileges) {
            CollectionsUtil.putMapList(privilegeMap, privilege.getUserId(), privilege);
        }

        Map<Long, UserInfo> userMap = Maps.newHashMap();
        for (Long userId : privilegeMap.keySet()) {
            userMap.put(userId, accountService.getUserInfo(userId));
        }

        List<ApiStaff> apiStaffList = Lists.newArrayList();
        for (Map.Entry<Long, UserInfo> userEntry : userMap.entrySet()) {
            List<Privilege> privilegeList = privilegeMap.get(userEntry.getKey());
            List<ApiPrivilege> apiPrivilegeList = Lists.transform(privilegeList, new Function<Privilege, ApiPrivilege>() {
                @Override
                public ApiPrivilege apply(Privilege privilege) {
                    return new ApiPrivilege(privilege);
                }
            });

            apiStaffList.add(new ApiStaff(new ApiUser(userEntry.getValue()), apiPrivilegeList));
        }

        return apiStaffList;
    }

    @Path("staff_privilege.json")
    @POST
    @RestApiInfo(
            desc = "更新员工权限",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            rolePrivileges = {RolePrivilege.ADMIN},
            resultDemo = ApiStaff.class,
            errors = {RestExceptionFactor.USER_ID_NOT_EXISTED,
                    RestExceptionFactor.NO_RIGHTS}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public ApiStaff setStaffPrivilege(
            @FormParam("user_id")
            @ParamSpec(isRequired = true, spec = USER_ID_SPEC, desc = USER_ID_DESC)
            long userId,
            @FormParam("group")
            @ParamSpec(isRequired = true, spec = ENUM_COMPLETE_SPEC, desc = "权限组")
            GroupType groupType,
            @FormParam("privilege")
            @ParamSpec(isRequired = true, spec = PRIVILEGE_BITS_SPEC, desc = PRIVILEGE_BITS_DESC)
            int privilege,
            @HeaderParam(HEADER_USER_ID)
            long operatorId
    ) {
        UserInfo userInfo = checkAndGetUser(userId);
        Privilege operatorPrivilege = staffService.getPrivilege(operatorId, GroupType.SUPERVISOR);
        if (operatorPrivilege != null && Privilege.hasPrivileges(RolePrivilege.ADMIN.getBit(), operatorPrivilege.getPrivileges())) {
            // 超级管理员可以添加任何权限
            // do nothing
        } else {
            // 其他，只有groupType权限组的管理员只可以添加groupType的权限
            operatorPrivilege = staffService.getPrivilege(operatorId, groupType);
            if (operatorPrivilege == null || !Privilege.hasPrivileges(RolePrivilege.ADMIN.getBit(), operatorPrivilege.getPrivileges())) {
                throw new VirgoRestException(RestExceptionFactor.NO_RIGHTS);
            }
        }

        if (!staffService.insertOrUpdatePrivilege(userId, groupType, privilege)) {
            throw new VirgoRestException(RestExceptionFactor.INTERNAL_SERVER_ERROR);
        }
        List<Privilege> userPrivileges = staffService.getPrivileges(userId);
        List<ApiPrivilege> apiPrivilegeList = Lists.transform(userPrivileges, new Function<Privilege, ApiPrivilege>() {
            @Override
            public ApiPrivilege apply(Privilege privilege) {
                return new ApiPrivilege(privilege);
            }
        });

        return new ApiStaff(new ApiUser(userInfo), apiPrivilegeList);
    }

    private UserInfo checkAndGetUser(long userId) {
        UserInfo userInfo = accountService.getUserInfo(userId);

        if (userInfo == null) {
            throw new VirgoRestException(RestExceptionFactor.USER_ID_NOT_EXISTED);
        }

        return userInfo;
    }
}
