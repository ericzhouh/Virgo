package com.winterfarmer.virgo.account.model;

import com.winterfarmer.virgo.common.util.EnumUtil;

/**
 * Created by yangtianhang on 15-4-16.
 */
public enum RolePrivilege {
    VIEW("查", 0, 1),
    MERGE("增/修改", 1, 1 << 1),
    DELETE("删", 2, 1 << 2),
    ADMIN("管理", 3, 1 << 3),;

    private final String name;
    private final int index;
    private final int bit;

    private RolePrivilege(String name, int index, int bit) {
        this.name = name;
        this.index = index;
        this.bit = bit;
    }

    public boolean hasPrivilege(int privilegeBit) {
        return (privilegeBit & this.bit) != 0;
    }

    public int getBit() {
        return bit;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public static RolePrivilege valueByIndex(int index) {
        for (RolePrivilege rolePrivilege : RolePrivilege.values()) {
            if (rolePrivilege.getIndex() == index) {
                return rolePrivilege;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return EnumUtil.toString(GroupType.class, this.name, this.index);
    }

    public static void main(String[] args) {
        System.out.println(RolePrivilege.ADMIN.hasPrivilege(1));
        System.out.println(RolePrivilege.ADMIN.hasPrivilege(1 << 3));
        System.out.println(RolePrivilege.ADMIN.hasPrivilege(0xffffff));
        System.out.println(RolePrivilege.ADMIN.hasPrivilege((1 << 3) + (1 << 2)));
        System.out.println(RolePrivilege.ADMIN.hasPrivilege((1 << 3) + (1 << 4)));
        System.out.println(1 << 4);
    }
}
