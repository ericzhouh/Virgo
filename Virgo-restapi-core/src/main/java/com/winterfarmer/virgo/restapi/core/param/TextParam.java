package com.winterfarmer.virgo.restapi.core.param;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by yangtianhang on 15-1-9.
 */
public abstract class TextParam extends AbstractParamSpec<String> {
    @Override
    public boolean isCompatible(Class<?> clazz) {
        return clazz == String.class;
    }

    @Override
    public String parse(String strParam) {
        return strParam;
    }

    //    @Override
//    protected boolean isValidSpecValue(String specValue) {
//        specValue = StringUtils.trim(specValue);
//        String[] strings = specValue.split("~");
//        try {
//            Integer.parseInt(StringUtils.trim(strings[0]));
//            Integer.parseInt(StringUtils.trim(strings[1]));
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }

    protected static int[] parseLengthRange(String specValue) {
        String lengthRangeSpec = StringUtils.trim(specValue.split(",")[0]);
        String[] lengthRangeStrs = StringUtils.split(lengthRangeSpec, "~");
        return new int[] {Integer.parseInt(lengthRangeStrs[0]), Integer.parseInt(lengthRangeStrs[1])};
    }

    protected static String parseExt(String specValue) {
        return StringUtils.trim(specValue.split(",")[1]);
    }

    protected static String createSpecValue(int minLength, int maxLength) {
        return minLength + "~" + maxLength;
    }

    protected static String createSpecValue(int minLength, int maxLength, String ext) {
        if (StringUtils.isEmpty(ext)) {
            return minLength + "~" + maxLength;
        } else {
            return minLength + "~" + maxLength + ", " + ext;
        }
    }
}
