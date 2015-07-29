package com.winterfarmer.virgo.qiniu.service;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.winterfarmer.virgo.qiniu.BucketType;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Created by yangtianhang on 15/5/20.
 */
@Service("qiniuService")
public class QiniuServiceImpl implements QiniuService {
    private static final String ACCESS_KEY = "1DAfo7Xx2yRt9_o0W8DeVxYzFmxLXDCD0CNOIywU";
    private static final String SECRET_KEY = "Ye79V0TWj-qdDXhqP3DLC5hHky_h-hdEBnxN4HU1";

    private static final Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);

    @Override
    public String getUpToken(BucketType bucketType) {
        return auth.uploadToken(bucketType.getBizName(), null, bucketType.getExpireS(), null, true);
    }

    @Override
    public String getOneImageUpToken(BucketType bucketType, long id) {
        StringMap policy = new StringMap();
        String key = Long.toString(id);
        policy.put("saveKey", key);
        return auth.uploadToken(bucketType.getBizName(), Long.toString(id), bucketType.getExpireS(), policy);
    }

    public static void main(String[] args) {
        QiniuServiceImpl qiniuService = new QiniuServiceImpl();
        String token = qiniuService.getOneImageUpToken(BucketType.test, 1000);
        System.out.println(token);
        File file = new File("/Users/yangtianhang/Documents/1.pic.jpg");
        MyRet ret = qiniuService.testUpload(token, 1000 + "", file);
        System.out.println("fsize=" + ret.fsize);
        System.out.println("key=" + ret.key);
        System.out.println("hash=" + ret.hash);
        System.out.println("width=" + ret.width);
        System.out.println("height=" + ret.height);
    }

    public class MyRet {
        public long fsize;
        public String key;
        public String hash;
        public int width;
        public int height;
    }

    private MyRet testUpload(String token, String key, File file) {
        try {
            UploadManager uploadManager = new UploadManager();
            Response res = uploadManager.put(file, key, token);
            MyRet ret = res.jsonToObject(MyRet.class);
            System.out.println(ret);
            System.out.println(res.toString());
            System.out.println(res.bodyString());
            return ret;
        } catch (QiniuException e) {
            Response r = e.response;
            // 请求失败时简单状态信息
            System.out.println(r.toString());
            try {
                // 响应的文本信息
                System.out.println(r.bodyString());
            } catch (QiniuException e1) {
                System.out.println(e1.getMessage());
            }
        }

        return null;
    }
}
