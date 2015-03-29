package com.winterfarmer.virgo.restapi.core.param;

import java.text.DecimalFormat;

/**
 * Created by yangtianhang on 15-1-9.
 */
public class DoubleParam extends NumericParam<Double> {
    private DoubleParam(String specValue) {
        super(specValue);
    }

    public DoubleParam(double min, boolean isMinOpen, double max, boolean isMaxOpen) {
        super(min, isMinOpen, max, isMaxOpen);
    }

    @Override
    public String sample() {
        DecimalFormat nf = new DecimalFormat("#.###");
        return nf.format(min + (max - min) / 2.0);
    }

    @Override
    public boolean isValid(String value) {
        double v = Double.parseDouble(value);

        if (isMinOpen && v <= min || v < min) {
            return false;
        }

        if (isMaxOpen && v >= max || v > max) {
            return false;
        }

        return true;
    }

    @Override
    public Double parse(String strParam) {
        return Double.parseDouble(strParam);
    }

    @Override
    public boolean isCompatible(Class<?> clazz) {
        return clazz == double.class || clazz == Double.class;
    }

    @Override
    public String getName() {
        return specName();
    }

    public static String specName() {
        return "Double";
    }

    @Override
    protected Double parseNumber(String number) {
        return Double.parseDouble(number);
    }

    @Override
    protected int compare(Double a, Double b) {
        return a.equals(b) ? 0 : (a < b ? -1 : 1);
    }

    public static DoubleParam instanceOf(String specValue) {
        return new DoubleParam(specValue);
    }
}
