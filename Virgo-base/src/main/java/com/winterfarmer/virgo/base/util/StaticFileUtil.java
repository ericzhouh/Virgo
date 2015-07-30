package com.winterfarmer.virgo.base.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by yangtianhang on 15/6/4.
 */
public class StaticFileUtil {
    private static final String BASE_PORTRAIT_URL = "http://7xj66j.com1.z0.glb.clouddn.com/";

    public static String getPortraitUrl(String imageId, int gender) {
        if (StringUtils.isBlank(imageId)) {
            if (gender == 0) {
                return "http://7xjoye.com2.z0.glb.qiniucdn.com/default-portrait-male.jpg";
            } else {
                return "http://7xjoye.com2.z0.glb.qiniucdn.com/default-portrait-female.jpg";
            }
        } else {
            return BASE_PORTRAIT_URL + imageId;
        }
    }
}
