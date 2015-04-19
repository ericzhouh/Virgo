package com.winterfarmer.virgo.aggregator.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.winterfarmer.virgo.base.annotation.ApiField;
import com.winterfarmer.virgo.base.annotation.ApiMode;

import java.util.List;

/**
 * Created by yangtianhang on 15-4-18.
 */
@ApiMode(desc = "员工信息")
public class ApiStaff {
    @JSONField(name = "user")
    @ApiField(desc = "用户信息")
    ApiUser user;

    @JSONField(name = "privileges")
    @ApiField(desc = "权限信息")
    List<ApiPrivilege> privileges;

    public ApiStaff(ApiUser user, List<ApiPrivilege> privileges) {
        this.user = user;
        this.privileges = privileges;
    }

    public ApiUser getUser() {
        return user;
    }

    public void setUser(ApiUser user) {
        this.user = user;
    }

    public List<ApiPrivilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<ApiPrivilege> privileges) {
        this.privileges = privileges;
    }
}
