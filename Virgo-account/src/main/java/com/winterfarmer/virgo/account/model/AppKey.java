package com.winterfarmer.virgo.account.model;

/**
 * Created by yangtianhang on 15-3-28.
 */
public enum AppKey {
    // 管理后台
    ADMIN_WEB(1000),

    // 我行
    WOXING_WEB(1005),
    WOXING_IOS(1006),
    WOXING_ANDROID(1007);

    private final int index;

    AppKey(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public static boolean isValidAppKey(int appKeyIndex) {
        switch (appKeyIndex) {
            case 1000:
            case 1005:
            case 1006:
            case 1007:
                return true;
            default:
                return false;
        }
    }
}
