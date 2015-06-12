package com.winterfarmer.virgo.base.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by yangtianhang on 15/6/4.
 */
public class StaticFileUtil {
    private static final String BASE_PORTRAIT_URL = "http://7xj66j.com1.z0.glb.clouddn.com/";

    public static String getPortraitUrl(String imageId) {
        if (StringUtils.isEmpty(imageId)) {
            return null;
        } else {
            return BASE_PORTRAIT_URL + imageId;
        }
    }
}
