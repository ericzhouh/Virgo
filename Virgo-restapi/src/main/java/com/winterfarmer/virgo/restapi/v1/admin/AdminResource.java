package com.winterfarmer.virgo.restapi.v1.admin;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.winterfarmer.virgo.account.model.GroupType;
import com.winterfarmer.virgo.account.model.Privilege;
import com.winterfarmer.virgo.account.model.RolePrivilege;
import com.winterfarmer.virgo.account.model.User;
import com.winterfarmer.virgo.account.service.AccountService;
import com.winterfarmer.virgo.account.service.StaffService;
import com.winterfarmer.virgo.aggregator.model.ApiPrivilege;
import com.winterfarmer.virgo.aggregator.model.ApiStaff;
import com.winterfarmer.virgo.aggregator.model.ApiUser;
import com.winterfarmer.virgo.base.model.CommonResult;
import com.winterfarmer.virgo.common.util.CollectionsUtil;
import com.winterfarmer.virgo.restapi.BaseResource;
import com.winterfarmer.virgo.restapi.core.annotation.ParamSpec;
import com.winterfarmer.virgo.restapi.core.annotation.RestApiInfo;
import com.winterfarmer.virgo.restapi.core.exception.RestExceptionFactor;
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
@Component("adminResource")
public class AdminResource extends BaseResource {
    @Resource(name = "staffService")
    StaffService staffService;

    @Resource(name = "accountService")
    AccountService accountService;

    // list员工
    @Path("list_staff.json")
    @GET
    @RestApiInfo(
            desc = "根据手机号发送验证码",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            groupType = GroupType.PUBLIC,
            rolePrivileges = {RolePrivilege.VIEW},
            errors = {}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApiStaff> getStaff(
            @QueryParam("group")
            @ParamSpec(isRequired = false, spec = ENUM_COMPLETE_SPEC, desc = "权限组")
            GroupType groupType,
            @QueryParam("page")
            @ParamSpec(isRequired = false, spec = NORMAL_PAGE_SPEC, desc = NORMAL_PAGE_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_COUNT)
            int page,
            @QueryParam("count")
            @ParamSpec(isRequired = false, spec = NORMAL_COUNT_SPEC, desc = NORMAL_COUNT_DESC)
            @DefaultValue(NORMAL_DEFAULT_PAGE_COUNT)
            int count) {
        List<Privilege> privileges = staffService.listStaffPrivilege(groupType, page, count);
        Map<Long, List<Privilege>> privilegeMap = Maps.newHashMap();
        for (Privilege privilege : privileges) {
            CollectionsUtil.putMapList(privilegeMap, privilege.getUserId(), privilege);
        }

        Map<Long, User> userMap = Maps.newHashMap();
        for (Long userId : privilegeMap.keySet()) {
            userMap.put(userId, accountService.getUser(userId));
        }

        List<ApiStaff> apiStaffList = Lists.newArrayList();
        for (Map.Entry<Long, User> userEntry : userMap.entrySet()) {
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


    // 设置某个人员的权限

    //
}
