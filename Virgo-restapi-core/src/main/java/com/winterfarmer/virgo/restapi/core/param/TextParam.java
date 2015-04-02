package com.winterfarmer.virgo.restapi.core.param;

import com.winterfarmer.virgo.restapi.core.exception.ParamSpecException;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by yangtianhang on 15-1-9.
 */
public abstract class TextParam extends AbstractParamSpec<String> {
    protected final int minLength;
    protected final int maxLength;

    public TextParam(String lengthSpecValue) {
        String[] split = StringUtils.split(lengthSpecValue);
        this.minLength = Integer.parseInt(StringUtils.trim(split[0]));
        this.maxLength = Integer.parseInt(StringUtils.trim(split[1]));
        checkSpecLength(minLength, maxLength);
    }

    public TextParam(int minLength, int maxLength) {
        this.minLength = minLength;
        this.maxLength = maxLength;
        checkSpecLength(minLength, maxLength);
    }

    private static void checkSpecLength(int minLength, int maxLength) {
        if (minLength < 0 || minLength > maxLength) {
            throw new ParamSpecException("Invalid StringParam length limit,  minLength:" + minLength + ", maxLength:" + maxLength);
        }
    }

    @Override
    public boolean isCompatible(Class<?> clazz) {
        return clazz == String.class;
    }

    @Override
    public String parse(String strParam) {
        return strParam;
    }

    protected static int[] parseLengthRange(String specValue) {
        String lengthRangeSpec = StringUtils.trim(specValue.split(",")[0]);
        String[] lengthRangeStrs = StringUtils.split(lengthRangeSpec, "~");
        return new int[]{Integer.parseInt(lengthRangeStrs[0]), Integer.parseInt(lengthRangeStrs[1])};
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
