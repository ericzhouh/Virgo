package com.winterfarmer.virgo.base.util;

/**
 * Created by yangtianhang on 15/6/4.
 */
public class StaticFileUtil {
    private static final String BASE_PORTRAIT_URL = "http://7xj66j.com1.z0.glb.clouddn.com/";

    public static String getPortraitUrl(String imageId) {
        return BASE_PORTRAIT_URL + imageId;
    }
}
