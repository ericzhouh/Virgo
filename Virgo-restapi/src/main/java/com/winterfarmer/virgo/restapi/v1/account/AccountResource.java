package com.winterfarmer.virgo.restapi.v1.account;

import com.winterfarmer.virgo.account.model.AccessToken;
import com.winterfarmer.virgo.account.service.AccountService;
import com.winterfarmer.virgo.log.VirgoLogger;
import com.winterfarmer.virgo.restapi.core.annotation.ParamSpec;
import com.winterfarmer.virgo.restapi.core.annotation.RestApiInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by yangtianhang on 15-3-30.
 */
@Path("account")
@Component("accountResource")
public class AccountResource {
    @Resource(name = "accountService")
    AccountService accountService;

    @Path("test.json")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        VirgoLogger.debug("this is debug");
        VirgoLogger.info("this is info");
        VirgoLogger.warn("this is warn");
        VirgoLogger.error("this is error");
        return "yes";
    }

    @Path("access_token.json")
    @POST
    @RestApiInfo(authPolicy = RestApiInfo.AuthPolicy.PUBLIC)
    @Produces(MediaType.APPLICATION_JSON)
    public AccessToken signUp(
            @FormParam("mobile_number")
            @ParamSpec(isRequired = true, spec = "mobile", desc = "")
            String mobileNumber,
            @FormParam("password")
            @ParamSpec(isRequired = true, spec = "string:6~24", desc = "")
            String password,
            @FormParam("nick_name")
            @ParamSpec(isRequired = true, spec = "UnicodeString:2~10", desc = "")
            String nickName,
            @FormParam("open_token")
            @ParamSpec(isRequired = false, spec = "UnicodeString:2~10", desc = "")
            String openToken,
            @FormParam("app_key")
            @ParamSpec(isRequired = false, spec = "int:[1000,2000]", desc = "")
            int appKey) {
        return accountService.signUpByMobile(mobileNumber, password, nickName,
                openToken, appKey);
    }

//    @Path("access_token.json")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @RestApiInfo()
//    public AccessToken getAccessToken(
//            @QueryParam("access_token")
//            @ParamSpec(isRequired = true, spec = "String:36~36", desc = "")
//            String accessTokenString
//    ) {
//        AccessToken accessToken = new AccessToken();
//        accessToken.setAppKey(100);
//        accessToken.setToken("00000");
//        return accessToken;
//    }

    @Path("testing.json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RestApiInfo(authPolicy = RestApiInfo.AuthPolicy.OAUTH)
    public AccessToken testing(
    ) {
        AccessToken accessToken = new AccessToken();
        accessToken.setAppKey(100);
        accessToken.setToken("00000");
        return accessToken;
    }
}
