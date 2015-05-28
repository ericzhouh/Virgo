package com.winterfarmer.virgo.account.model;

import com.winterfarmer.virgo.common.util.EnumUtil;

/**
 * Created by yangtianhang on 15-2-26.
 */
public enum PlatformType {
    RESERVED("reserved", 0),
    SINA("sina", 1),
    QQ("qq", 2),
    WE_CHAT("we_chat", 3),
    MOBILE("mobile", 4),
    EMAIL("email", 5);

    private final String name;
    private final int index;

    PlatformType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public static PlatformType valueByIndex(int index) {
        for (PlatformType platformType : PlatformType.values()) {
            if (platformType.getIndex() == index) {
                return platformType;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return EnumUtil.toString(PlatformType.class, this.name, this.index);
    }
}
