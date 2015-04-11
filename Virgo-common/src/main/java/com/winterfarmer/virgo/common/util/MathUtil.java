package com.winterfarmer.virgo.common.util;

/**
 * Created by yangtianhang on 15-4-10.
 */
public class MathUtil {
    public static final double POSITIVE_ZERO = 0.0000001;
    public static final double NEGATIVE_ZERO = -0.0000001;

    public static boolean isEquale(double a, double b) {
        double diff = a - b;
        return diff < POSITIVE_ZERO && diff > NEGATIVE_ZERO;
    }

    public static boolean isEquale(float a, float b) {
        float diff = a - b;
        return diff < POSITIVE_ZERO && diff > NEGATIVE_ZERO;
    }
}
