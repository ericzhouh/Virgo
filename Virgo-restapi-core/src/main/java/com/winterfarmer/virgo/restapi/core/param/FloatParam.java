package com.winterfarmer.virgo.restapi.core.param;

import java.text.DecimalFormat;

/**
 * Created by yangtianhang on 15-1-9.
 */
public class FloatParam extends NumericParam<Float> {
    private FloatParam(String specValue) {
        super(specValue);
    }

    private FloatParam(Float min, boolean isMinOpen, Float max, boolean isMaxOpen) {
        super(min, isMinOpen, max, isMaxOpen);
    }

    public static String specName() {
        return "Float";
    }

    public static FloatParam instanceOf(String specValue) {
        return new FloatParam(specValue);
    }

    @Override
    public String sample() {
        DecimalFormat nf = new DecimalFormat("#.###");
        return nf.format(min + (max - min) / 2.0);
    }

    @Override
    public boolean isValid(String value) {
        float v = Float.parseFloat(value);

        if (isMinOpen && v <= min || v < min) {
            return false;
        }

        if (isMaxOpen && v >= max || v > max) {
            return false;
        }

        return true;
    }

    @Override
    public Float parse(String strParam) {
        return Float.parseFloat(strParam);
    }

    @Override
    public boolean isCompatible(Class<?> clazz) {
        return clazz == float.class || clazz == Float.class;
    }

    @Override
    public String getName() {
        return specName();
    }

    @Override
    protected Float parseNumber(String number) {
        return Float.parseFloat(number);
    }

    @Override
    protected int compare(Float a, Float b) {
        return a.equals(b) ? 0 : (a < b ? -1 : 1);
    }
}
