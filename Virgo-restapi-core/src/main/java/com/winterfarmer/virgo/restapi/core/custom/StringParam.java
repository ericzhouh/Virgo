package com.winterfarmer.virgo.restapi.core.custom;

import com.winterfarmer.virgo.restapi.core.exception.ParamSpecException;
import com.winterfarmer.virgo.restapi.core.param.TextParam;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yangtianhang on 15-1-9.
 */
public class StringParam extends TextParam {
    private final int minLength;
    private final int maxLength;
    private final Pattern pattern;
    private final String spec;
    private final String specValue;

    public static String lengthLimit(int minLength, int maxLength) {
        return new StringParam(minLength, maxLength, null).getSpec();
    }

    public static String pattern(int minLength, int maxLength, String regex) {
        return new StringParam(minLength, maxLength, regex).getSpec();
    }

    private StringParam(int minLength, int maxLength, String regex) {
        if (minLength < 0 || maxLength < minLength) {
            throw new ParamSpecException("Invalid StringParam length limit, minLength: " + minLength + " maxLength: " + maxLength);
        }

        this.minLength = minLength;
        this.maxLength = maxLength;
        if (regex != null) {
            pattern = Pattern.compile(regex);
        } else {
            pattern = null;
        }

        this.specValue = createSpecValue(minLength, maxLength, pattern != null ? "pattern=" + pattern : null);
        this.spec = createSpec(getName(), getValue());
    }

    @Override
    public String description() {
        if (this.pattern != null) {
            return "StringParam pattern: " + pattern.pattern() + ", length range: [" + minLength + ", " + maxLength + "]";
        } else {
            return "StringParam length range: [" + minLength + ", " + maxLength + "]";
        }
    }

    @Override
    public String sample() {
        return "normal string";
    }

    @Override
    public boolean isValid(String value) {
        boolean isLengthValid = value.length() >= minLength && value.length() <= maxLength;
        if (!isLengthValid) {
            return false;
        }

        if (pattern != null) {
            Matcher m = pattern.matcher(value);
            return m.matches();
        }

        return true;
    }

    @Override
    public String getName() {
        return "String";
    }

    @Override
    public String getSpec() {
        return spec;
    }

    @Override
    public String getValue() {
        return specValue;
    }
}
