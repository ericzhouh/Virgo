package com.winterfarmer.virgo.restapi.core.custom;

import com.winterfarmer.virgo.restapi.core.param.TextParam;

/**
 * api字符长度检查 汉字算2个,英文字符和数字算1个
 * Created by yangtianhang on 15-1-9.
 */
public class UnicodeStringParam extends TextParam {
    private final int minLength;
    private final int maxLength;

    public static UnicodeStringParam lengthLimit(int minLength, int maxLength) {
        return new UnicodeStringParam(minLength, maxLength);
    }

    public UnicodeStringParam(int minLength, int maxLength) {
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    public String description() {
        return "UnicodeStringParam length range: [" + minLength + ", " + maxLength + "]";
    }

    @Override
    public String sample() {
        return "unicode string";
    }

    @Override
    public boolean isValid(String value) {
        int len = unicodeLength(value);
        return len >= minLength && len <= maxLength;
    }

    @Override
    public String getName() {
        return "UnicodeString";
    }

    private int unicodeLength(String str) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        int len = str.length();
        int index = 0;
        char c;

        for (int i = 0; i < len; i++) {
            c = str.charAt(i);
            if ((c > 0xFF)) {
                index += 2;
            } else {
                index++;
            }
        }

        return index;
    }

    @Override
    public String getSpec() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }

    public UnicodeStringParam instanceOf(String specValue) {
        return null;
    }
}
