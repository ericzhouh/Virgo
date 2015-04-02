package com.winterfarmer.virgo.restapi.core.param;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yangtianhang on 15-1-9.
 */
public class StringParam extends TextParam {
    private final Pattern pattern;

    private final String spec;
    private final String specValue;

    public StringParam(String specValue) {
        super(getLengthSpecValue(specValue));
        String[] split = StringUtils.split(specValue, ",", 2);
        if (split.length == 2) {
            pattern = Pattern.compile(split[1]);
        } else {
            pattern = null;
        }

        this.specValue = createSpecValue(minLength, maxLength, pattern != null ? "pattern=" + pattern : null);
        this.spec = createSpec(getName(), getValue());
    }

    private static String getLengthSpecValue(String specValue) {
        return StringUtils.split(specValue, ",", 2)[0];
    }

    public static String lengthLimit(int minLength, int maxLength) {
        return new StringParam(minLength, maxLength, null).getSpec();
    }

    public static String pattern(int minLength, int maxLength, String regex) {
        return new StringParam(minLength, maxLength, regex).getSpec();
    }

    private StringParam(int minLength, int maxLength, String regex) {
        super(minLength, maxLength);

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
