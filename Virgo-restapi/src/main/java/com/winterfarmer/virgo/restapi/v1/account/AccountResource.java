package com.winterfarmer.virgo.restapi.v1.account;

import com.winterfarmer.virgo.account.model.AccessToken;
import com.winterfarmer.virgo.account.service.AccountService;
import com.winterfarmer.virgo.log.VirgoLogger;
import com.winterfarmer.virgo.restapi.core.annotation.ParamSpec;
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

    //    @Path("access_token.json")
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    public AccessToken signUp(String mobileNumber, String password, String nickName,
//                              String openToken, int appKey) {
//        return null;
//    }
//
    @Path("access_token.json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AccessToken getAccessToken(
            @QueryParam("access_token")
            @ParamSpec(isRequired = true, spec = "String:[36:36]", desc = "")
            String accessTokenString
    ) {
        AccessToken accessToken = new AccessToken();
        accessToken.setAppKey(100);
        accessToken.setToken("00000");
        return accessToken;
    }
}
