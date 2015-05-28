package com.winterfarmer.virgo.account.model;

import com.winterfarmer.virgo.common.util.EnumUtil;

import java.util.Map;

/**
 * Created by yangtianhang on 15/5/28.
 */
public enum UserType implements EnumUtil.VirgoEnum {
    NORMAL("普通用户", 0),
    EXPERT("专家用户", 1);

    private final String name;
    private final int index;

    private static final Map<Integer, UserType> enumMap = EnumUtil.getEnumMap(UserType.class);

    UserType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public static UserType valueByIndex(int index) {
        return enumMap.get(index);
    }

    @Override
    public String toString() {
        return EnumUtil.toString(this);
    }
}
