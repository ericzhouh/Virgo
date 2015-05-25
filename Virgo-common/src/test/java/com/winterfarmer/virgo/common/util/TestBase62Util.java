package com.winterfarmer.virgo.common.util;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by yangtianhang on 15-3-27.
 */
public class TestBase62Util {
    @Test
    public void testEncodeAndDecode() {
        Random random = new Random(System.currentTimeMillis());
        Set<Long> wrongs = Sets.newHashSet();

        for (int i = 0; i < 1000; ++i) {
            long number = random.nextLong();
            if (number < 0) {
                number = -number;
            }
            String encode = Base62Util.encodeMaxBytes(number);
            long decode = Base62Util.decode(encode);
            System.out.println("number: " + number + " encode: " + encode + " decode: " + decode);
            System.out.println(number == decode ? "Right" : "Wrong");
            if (number != decode) {
                wrongs.add(number);
            }
        }

        Assert.isTrue(wrongs.isEmpty());
    }

    @Test
    public void testNickNamePatter() {
        String regex = "^[a-zA-Z0-9\u4E00-\u9FA5-_]+$";
        Pattern pattern = Pattern.compile(regex);
        String s1 = "12345";
        System.out.println(s1 + " " + pattern.matcher(s1).matches());
        String s2 = "恒银上哈";
        System.out.println(s2 + " " + pattern.matcher(s2).matches());
        String s3 = "abcabc";
        System.out.println(s3 + " " + pattern.matcher(s3).matches());
        String s4 = "12345ab恒银上哈cabc";
        System.out.println(s4 + " " + pattern.matcher(s4).matches());
        String s5 = "12345ab恒银上哈cabc+";
        System.out.println(s5 + " " + pattern.matcher(s5).matches());
        String s6 = "12345ab恒_银上哈cabc-";
        System.out.println(s6 + " " + pattern.matcher(s6).matches());
        String s7 = "12345ab恒-银上哈cabc_";
        System.out.println(s7 + " " + pattern.matcher(s7).matches());
        String s8 = "12345ab恒-银上哈cabc_，";
        System.out.println(s8 + " " + pattern.matcher(s8).matches());
        System.out.println("============================");
        regex = "^\\d+$";
        pattern = Pattern.compile(regex);
        String s = "12345";
        System.out.println(s + " " + pattern.matcher(s).matches());
        s = "12345a";
        System.out.println(s + " " + pattern.matcher(s).matches());

    }
}
