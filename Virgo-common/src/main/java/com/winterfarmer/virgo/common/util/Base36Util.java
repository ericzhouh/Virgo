package com.winterfarmer.virgo.common.util;

/**
 * Created by yangtianhang on 15/6/2.
 */
public class Base36Util {
    private static final byte[] BASE36_BYTES = new byte[]{
            'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm',
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
    };

    public static final String encode(long number) {
        if (number < 0) {
            throw new IllegalArgumentException("Number(Base36) must be positive: " + number);
        }

        StringBuilder sb = new StringBuilder();
        while (number != 0) {
            sb.append((char) BASE36_BYTES[(int) (number % BASE36_BYTES.length)]);
            number /= BASE36_BYTES.length;
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(encode(System.currentTimeMillis()));
    }
}
