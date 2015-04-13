package com.winterfarmer.virgo.account.model;

/**
 * Created by yangtianhang on 15-3-28.
 */
public enum AppKey {
    mobile(1024);

    private final int index;

    AppKey(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public static boolean isValidAppKey(int appKeyIndex) {
        return false;
    }
}
