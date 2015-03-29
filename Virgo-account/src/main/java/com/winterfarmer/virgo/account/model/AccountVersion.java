package com.winterfarmer.virgo.account.model;

import com.winterfarmer.virgo.common.util.EnumUtil;

/**
 * Created by yangtianhang on 15-3-26.
 */
public enum AccountVersion {
    PRIMITIVE("primitive", 0), // Outsourcing
    SALT_SHA256("salt_sha256", 1); // salt + SHA256

    private final int index;
    private final String name;

    private AccountVersion(String name, int index) {
        this.index = index;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public static AccountVersion valueByIndex(int index) {
        switch (index) {
            case 0:
                return PRIMITIVE;
            case 1:
                return SALT_SHA256;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return EnumUtil.toString(PlatformType.class, this.name, this.index);
    }
}
