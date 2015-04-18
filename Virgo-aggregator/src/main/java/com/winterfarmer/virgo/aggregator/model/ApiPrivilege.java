package com.winterfarmer.virgo.aggregator.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.winterfarmer.virgo.account.model.GroupType;
import com.winterfarmer.virgo.account.model.Privilege;
import com.winterfarmer.virgo.base.annotation.ApiField;
import com.winterfarmer.virgo.base.annotation.ApiMode;

/**
 * Created by yangtianhang on 15-4-18.
 */
@ApiMode(desc = "权限")
public class ApiPrivilege {
    @JSONField(name = "group")
    @ApiField(desc = "group")
    private int groupType;

    @JSONField(name = "privileges")
    @ApiField(desc = "权限bit位")
    private int privileges; // bit位表示

    public ApiPrivilege(Privilege privilege) {
        this.privileges = privilege.getPrivileges();
        this.groupType = privilege.getGroupType().getIndex();
    }

    public ApiPrivilege(int privileges, int groupType) {
        this.privileges = privileges;
        this.groupType = groupType;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType.getIndex();
    }

    public int getPrivileges() {
        return privileges;
    }

    public void setPrivileges(int privileges) {
        this.privileges = privileges;
    }
}
