package com.winterfarmer.virgo.restapi.v1.qiniu;

import com.google.common.collect.ImmutableList;
import com.winterfarmer.virgo.base.model.CommonResult;
import com.winterfarmer.virgo.base.service.IdService;
import com.winterfarmer.virgo.qiniu.BucketType;
import com.winterfarmer.virgo.qiniu.service.QiniuService;
import com.winterfarmer.virgo.restapi.BaseResource;
import com.winterfarmer.virgo.restapi.core.annotation.ParamSpec;
import com.winterfarmer.virgo.restapi.core.annotation.ResourceOverview;
import com.winterfarmer.virgo.restapi.core.annotation.RestApiInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.List;

/**
 * Created by yangtianhang on 15/6/8.
 */
@Path("qiniu")
@ResourceOverview(desc = "七牛文件接口")
@Component("qiniuResource")
public class QiniuResource extends BaseResource {
    @Resource(name = "qiniuService")
    QiniuService qiniuService;

    @Resource(name = "idService")
    IdService idService;

    private static String qiniu = null;
    private static String plupload = null;

    @Path("token.json")
    @GET
    @RestApiInfo(
            desc = "上传的token",
            authPolicy = RestApiInfo.AuthPolicy.OAUTH,
            resultDemo = CommonResult.class,
            errors = {}
    )
    @Produces(MediaType.APPLICATION_JSON)
    public CommonResult getUploadUserImageToken(
            @HeaderParam(HEADER_USER_ID)
            long userId) {
        long id = idService.getId();
        String token = qiniuService.getOneImageUpToken(BucketType.app_user, id);
        return CommonResult.oneResultCommonResult("token", token);
    }

    @Path("qiniu.js")
    @GET
    @RestApiInfo(
            desc = "上传的token",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = CommonResult.class,
            errors = {}
    )
    @Produces(MediaType.TEXT_PLAIN)
    public String getQiniuJs() {
        if (qiniu == null) {
            qiniu = read("/home/ubuntu/temp/qiniu.js");
        }

        return qiniu;
    }

    @Path("plupload.full.min.js")
    @GET
    @RestApiInfo(
            desc = "上传的token",
            authPolicy = RestApiInfo.AuthPolicy.PUBLIC,
            resultDemo = CommonResult.class,
            errors = {}
    )
    @Produces(MediaType.TEXT_PLAIN)
    public String getUploadJs() {
        if (plupload == null) {
            plupload = read("/home/ubuntu/temp/plupload.full.min.js");
        }

        return plupload;
    }

    private String read(String path) {
        File file = new File(path);
        Long filelength = file.length();     //获取文件长度

        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "错了";
        } catch (IOException e) {
            e.printStackTrace();
            return "错了";
        }

        return new String(filecontent);//返回文件内容,默认编码
    }
}
