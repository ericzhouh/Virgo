package com.winterfarmer.virgo.qiniu;

/**
 * Created by yangtianhang on 15/5/20.
 */
public enum BucketType {
    test("yiluxiangqian", BucketType.ONE_DAY_S),
    app_user(BucketType.BIZ_USER, BucketType.HALF_YEAR_S),
    app_knowledge(BucketType.BIZ_KNOWLEDGE, BucketType.HALF_YEAR_S),
    web_user(BucketType.BIZ_USER, BucketType.ONE_WEEK_S),
    web_knowledge(BucketType.BIZ_KNOWLEDGE, BucketType.ONE_WEEK_S);



    private static final int ONE_DAY_S = 60 * 60 * 24;
    private static final int HALF_YEAR_S = ONE_DAY_S * 30 * 6;
    private static final int ONE_WEEK_S = ONE_DAY_S * 7;

    private static final String BIZ_USER = "yiluxiangqian";
    private static final String BIZ_KNOWLEDGE = "yiluxiangqian";

    private final String bizName;
    private final long expireS;

    /**
     * @param bizName 空间名
     * @param expireS 有效时长，单位秒。
     */
    BucketType(String bizName, long expireS) {
        this.bizName = bizName;
        this.expireS = expireS;
    }

    public String getBizName() {
        return bizName;
    }

    public long getExpireS() {
        return expireS;
    }
}
