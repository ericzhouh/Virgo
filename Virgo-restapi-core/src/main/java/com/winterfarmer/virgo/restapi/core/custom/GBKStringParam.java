package com.winterfarmer.virgo.restapi.core.custom;

import com.winterfarmer.virgo.restapi.core.exception.ParamSpecException;
import com.winterfarmer.virgo.restapi.core.param.TextParam;

/**
 * gbk字符长度检查 汉字算1个,英文字符和数字算0.5个
 * 1 < gbkLeng(abc) <2
 * Created by yangtianhang on 15-1-9.
 */
public class GBKStringParam extends TextParam {
    private final int minLength;
    private final int maxLength;
    private final String specValue;
    private final String spec;

    public static GBKStringParam lengthLimit(int minLength, int maxLength) {
        return new GBKStringParam(minLength, maxLength);
    }

    private GBKStringParam(int minLength, int maxLength) {
        if (minLength < 0 || maxLength < minLength) {
            throw new ParamSpecException("Invalid GBKStringParam length limit, minLength: " + minLength + " maxLength: " + maxLength);
        }

        this.minLength = minLength;
        this.maxLength = maxLength;
        this.specValue = createSpecValue(minLength, maxLength);
        this.spec = createSpec(getName(), getValue());
    }

    @Override
    public String description() {
        return "GBKStringParam length range: [" + minLength + ", " + maxLength + "]";
    }

    @Override
    public String sample() {
        return "gbk string";
    }

    @Override
    public boolean isValid(String value) {
        float len = gbkLength(value);
        return len >= minLength && len <= maxLength;
    }

    @Override
    public String getName() {
        return specName();
    }

    @Override
    public String getSpec() {
        return spec;
    }

    @Override
    public String getValue() {
        return specValue;
    }

    public static String specName() {
        return "GBKString";
    }

    /**
     * 计算字符串长度
     * 汉字算1个，英文字符和数字算0.5个
     *
     * @param str 待处理字符串
     * @return
     */
    private float gbkLength(String str) {
        if (str == null || str.length() == 0) {
            return 0;
        }

        int len = str.length();
        int index = 0;
        char c;

        for (int i = 0; i < len; i++) {
            c = str.charAt(i);
            if ((c >= 0x4E00 && c <= 0x9FFF)) {
                index += 2;
            } else {
                index++;
            }
        }

        return index / 2F;
    }
}
