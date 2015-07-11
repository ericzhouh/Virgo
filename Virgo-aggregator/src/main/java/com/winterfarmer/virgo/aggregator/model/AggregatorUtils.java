package com.winterfarmer.virgo.aggregator.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yangtianhang on 15/7/11.
 */
public class AggregatorUtils {
    private final static long ONE_MINUTE = 1000 * 60;
    private final static long ONE_HOUR = 1000 * 60 * 60;
    private final static long ONE_DAY = 1000 * 60 * 60 * 24;
    private final static long TWO_DAY = 1000 * 60 * 60 * 24 * 2;
    private final static long THREE_DAY = 1000 * 60 * 60 * 24 * 3;

    private final static SimpleDateFormat timeFormat = new SimpleDateFormat("HH点mm分");
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");

    public static String timeConverter(long time) {
        long current = System.currentTimeMillis();
        long diff = current - time;
        if (diff < ONE_MINUTE) {
            return "刚刚";
        } else if (diff < ONE_HOUR) {
            return "一小时内";
        } else if (diff < ONE_DAY) {
            return "今天 " + timeFormat.format(new Date(time));
        } else if (diff < TWO_DAY) {
            return "昨天";
        } else if (diff < THREE_DAY) {
            return "前天";
        } else {
            return dateFormat.format(new Date(time));
        }
    }

    public static void main(String[] args) {
        System.out.println(timeConverter(System.currentTimeMillis()));
        System.out.println(timeConverter(System.currentTimeMillis() - ONE_HOUR + 10000));
        System.out.println(timeConverter(System.currentTimeMillis() - ONE_DAY + 10000));
        System.out.println(timeConverter(System.currentTimeMillis() - TWO_DAY + 10000));
        System.out.println(timeConverter(System.currentTimeMillis() - THREE_DAY + 10000));
        System.out.println(timeConverter(System.currentTimeMillis() - THREE_DAY - THREE_DAY + 10000));
    }
}
