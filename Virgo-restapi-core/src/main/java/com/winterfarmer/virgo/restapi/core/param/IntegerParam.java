package com.winterfarmer.virgo.restapi.core.param;

/**
 * Created by yangtianhang on 15-1-9.
 */
public class IntegerParam extends NumericParam<Integer> {
    public IntegerParam(String specValue) {
        super(specValue);
    }

    public IntegerParam(Integer min, boolean isMinOpen, Integer max, boolean isMaxOpen) {
        super(min, isMinOpen, max, isMaxOpen);
    }

    @Override
    public String sample() {
        if (isMinOpen) {
            return Integer.toString(min + (max - min + 1) / 2);
        } else {
            return Integer.toString(min + (max - min) / 2);
        }
    }

    @Override
    public boolean isValid(String value) {
        int v = Integer.parseInt(value);

        if (isMinOpen && v <= min || v < min) {
            return false;
        }

        if (isMaxOpen && v >= max || v > max) {
            return false;
        }

        return true;
    }

    @Override
    public Integer parse(String strParam) {
        return Integer.parseInt(strParam);
    }

    @Override
    public boolean isCompatible(Class<?> clazz) {
        return clazz == int.class || clazz == Integer.class;
    }

    @Override
    public String getName() {
        return specName();
    }

    @Override
    protected Integer parseNumber(String number) {
        return Integer.parseInt(number);
    }

    @Override
    protected int compare(Integer a, Integer b) {
        return a - b;
    }

    public static String specName() {
        return "Int";
    }
}
