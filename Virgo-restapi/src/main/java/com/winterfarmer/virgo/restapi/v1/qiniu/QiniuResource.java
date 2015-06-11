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
            @QueryParam("count")
            @ParamSpec(isRequired = true, spec = "int:[1,37]", desc = "需要上传图片的个数")
            @DefaultValue("1")
            int count,
            @HeaderParam(HEADER_USER_ID)
            long userId) {
        String token = qiniuService.getUpToken(BucketType.app_user);
        ImmutableList<Long> idList = idService.getIds(count);
        return CommonResult.newCommonResult("token", token,
                "ids", idList);
    }
}
