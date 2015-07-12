package com.winterfarmer.virgo.common.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by yangtianhang on 15/5/15.
 */
public class StringUtil {
    public static final String COMMA = ",";

    public static long[] splitLongCommaString(String commaSplitString) {
        String[] splitStrings = StringUtils.split(commaSplitString, COMMA);
        if (ArrayUtils.isEmpty(splitStrings)) {
            return new long[0];
        } else {
            long[] longNums = new long[splitStrings.length];
            int i = 0;
            for (String splitString : splitStrings) {
                try {
                    longNums[i++] = Long.parseLong(splitString);
                } catch (NumberFormatException e) {
                    return null;
                }
            }

            return longNums;
        }
    }

    public static int[] splitIntCommaString(String commaSplitString) {
        String[] splitStrings = StringUtils.split(commaSplitString, COMMA);
        if (ArrayUtils.isEmpty(splitStrings)) {
            return new int[0];
        } else {
            int[] intNums = new int[splitStrings.length];
            int i = 0;
            for (String splitString : splitStrings) {
                try {
                    intNums[i++] = Integer.parseInt(StringUtils.trim(splitString));
                } catch (NumberFormatException e) {
                    return null;
                }
            }

            return intNums;
        }
    }

    public static String[] splitCommaString(String commaSplitString) {
        return StringUtils.split(commaSplitString, COMMA);
    }
}
