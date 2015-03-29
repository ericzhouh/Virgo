package com.winterfarmer.virgo.common.util;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.Random;
import java.util.Set;

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
}
