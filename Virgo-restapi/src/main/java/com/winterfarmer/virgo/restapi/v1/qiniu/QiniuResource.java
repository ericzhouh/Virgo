package com.winterfarmer.virgo.restapi.v1.qiniu;

import com.winterfarmer.virgo.base.model.CommonResult;
import com.winterfarmer.virgo.qiniu.BucketType;
import com.winterfarmer.virgo.qiniu.service.QiniuService;
import com.winterfarmer.virgo.restapi.BaseResource;
import com.winterfarmer.virgo.restapi.core.annotation.ResourceOverview;
import com.winterfarmer.virgo.restapi.core.annotation.RestApiInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by yangtianhang on 15/6/8.
 */
@Path("qiniu")
@ResourceOverview(desc = "七牛文件接口")
@Component("qiniuResource")
public class QiniuResource extends BaseResource {
    @Resource(name = "qiniuService")
    QiniuService qiniuService;

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
        String token = qiniuService.getUpToken(BucketType.test);
        return CommonResult.oneResultCommonResult("token", token);
    }
}
