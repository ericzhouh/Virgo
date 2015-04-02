package com.winterfarmer.virgo.restapi.core.param;

/**
 * Created by yangtianhang on 15-1-9.
 */
public class LongParam extends NumericParam<Long> {
    public LongParam(String specValue) {
        super(specValue);
    }

    @Override
    public String sample() {
        if (isMinOpen) {
            return Long.toString(min + (max - min + 1) / 2);
        } else {
            return Long.toString(min + (max - min) / 2);
        }
    }

    @Override
    public boolean isValid(String value) {
        long v = Long.parseLong(value);

        if (isMinOpen && v <= min || v < min) {
            return false;
        }

        if (isMaxOpen && v >= max || v > max) {
            return false;
        }

        return true;
    }

    @Override
    public Long parse(String strParam) {
        return Long.parseLong(strParam);
    }

    @Override
    public boolean isCompatible(Class<?> clazz) {
        return clazz == long.class || clazz == Long.class;
    }

    protected boolean isValidSpecNumber(String specNum) {
        try {
            Long.parseLong(specNum);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getName() {
        return "Long";
    }

    public static String specName() {
        return "Long";
    }

    @Override
    protected Long parseNumber(String number) {
        return Long.parseLong(number);
    }

    @Override
    protected int compare(Long a, Long b) {
        return a.equals(b) ? 0 : (a < b ? -1 : 1);
    }
}
