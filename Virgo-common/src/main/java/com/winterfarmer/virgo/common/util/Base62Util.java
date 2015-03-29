package com.winterfarmer.virgo.common.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Created by yangtianhang on 15-3-27.
 */
public class Base62Util {
    private static final byte[] KEYBOARD_BASE62_BYTES = new byte[]{
            'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm',
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
            'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B', 'N', 'M'
    };

    private static final int BASE62_LENGTH = KEYBOARD_BASE62_BYTES.length;

    private static final int[] BASE62_INDEX = new int[256];

    static {
        Arrays.fill(BASE62_INDEX, -1);
        int i = 0;
        for (byte b : KEYBOARD_BASE62_BYTES) {
            BASE62_INDEX[b] = i++;
        }
    }

    public static final int MAX_LONG_ENCODE_LENGTH = 11;// long(>0)经过encode之后, 最多需要11个byte

    private static final long[] BASE = new long[MAX_LONG_ENCODE_LENGTH];

    static {
        long base = 1;
        for (int i = 0; i < BASE.length; ++i) {
            BASE[i] = base;
            base *= BASE62_LENGTH;
        }
    }

    public static String encodeMaxBytes(long number) {
        return encode(number, MAX_LONG_ENCODE_LENGTH);
    }

    public static String encode(long number, int byteLen) {
        String code = encode(number);
        int supply = byteLen - code.length();
        if (supply > 0) {
            return code + StringUtils.repeat((char) KEYBOARD_BASE62_BYTES[0], supply);
        } else {
            return code;
        }
    }

    public static String encode(long number) {
        if (number < 0) {
            throw new IllegalArgumentException("Number(Base62) must be positive: " + number);
        }

        StringBuilder sb = new StringBuilder();
        while (number != 0) {
            sb.append((char) KEYBOARD_BASE62_BYTES[(int) (number % BASE62_LENGTH)]);
            number /= BASE62_LENGTH;
        }

        return sb.toString();
    }

    public static long decode(String string) {
        byte[] bytes = org.apache.commons.codec.binary.StringUtils.getBytesUsAscii(string);
        return decode(bytes);
    }

    public static long decode(byte[] bytes) {
        if (ArrayUtils.isEmpty(bytes) || bytes.length > MAX_LONG_ENCODE_LENGTH) {
            throw new IllegalArgumentException("base62 bytes must not be empty and length lt " + MAX_LONG_ENCODE_LENGTH);
        }

        long number = 0;
        for (int i = 0; i < bytes.length; ++i) {
            byte b = bytes[i];
            number += BASE[i] * BASE62_INDEX[b];
        }

        return number;
    }

    public static String convert(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((char) KEYBOARD_BASE62_BYTES[(((int) b) % BASE62_LENGTH + BASE62_LENGTH) % BASE62_LENGTH]);
        }

        return sb.toString();
    }
}