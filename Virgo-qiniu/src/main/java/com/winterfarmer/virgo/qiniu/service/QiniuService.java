package com.winterfarmer.virgo.qiniu.service;

import com.winterfarmer.virgo.qiniu.BucketType;

import java.io.File;

/**
 * Created by yangtianhang on 15/6/10.
 */
public interface QiniuService {
    String getUpToken(BucketType bucketType);

    String getOneImageUpToken(BucketType bucketType, long id);
}
