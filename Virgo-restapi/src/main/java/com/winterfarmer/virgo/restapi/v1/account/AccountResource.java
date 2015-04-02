package com.winterfarmer.virgo.restapi.v1.account;

import com.winterfarmer.virgo.account.model.AccessToken;
import com.winterfarmer.virgo.account.service.AccountService;
import com.winterfarmer.virgo.log.VirgoLogger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
//    @Path("access_token.json")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public AccessToken getAccessToken(String tokenString) {
//        AccessToken accessToken = new AccessToken();
//        accessToken.setAppKey(100);
//        accessToken.setToken("00000");
//        return null;
//    }
}
