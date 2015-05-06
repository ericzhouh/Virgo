package com.winterfarmer.virgo.restapi.core.filter.auth;

import com.winterfarmer.virgo.account.model.AccessToken;
import com.winterfarmer.virgo.account.service.AccountService;
import com.winterfarmer.virgo.account.service.StaffService;
import com.winterfarmer.virgo.log.VirgoLogger;
import com.winterfarmer.virgo.restapi.core.exception.RestExceptionFactor;
import com.winterfarmer.virgo.restapi.core.exception.VirgoRestException;
import com.winterfarmer.virgo.restapi.core.filter.ApplicationContextHolder;
import com.winterfarmer.virgo.restapi.core.request.ContainerRequestUtil;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.container.ContainerRequestContext;

/**
 * Created by yangtianhang on 15-3-29.
 */
public abstract class AccountFilter {
    protected AccountService accountService;

    protected StaffService staffService;

    protected AccountFilter() {
        accountService = ApplicationContextHolder.getBean("accountService");
        staffService = ApplicationContextHolder.getBean("staffService");
    }

    protected String getAccessTokenString(ContainerRequestContext requestContext) {
        return ContainerRequestUtil.getAccessToken(requestContext);
    }

    protected AccessToken getAccessTokenAndExplainToHeader(ContainerRequestContext requestContext) {
        String accessTokenString = ContainerRequestUtil.getAccessToken(requestContext);
        return getAccessTokenAndExplainToHeader(requestContext, accessTokenString);
    }

    // ExplainToHeader: set userId and appKey to header
    protected AccessToken getAccessTokenAndExplainToHeader(ContainerRequestContext requestContext, String accessTokenString) {
        if (StringUtils.isBlank(accessTokenString)) {
            throw new VirgoRestException(RestExceptionFactor.MISSING_ACCESS_TOKEN);
        }

        AccessToken accessToken = accountService.getAccessToken(accessTokenString);

        if (accessToken == null || !StringUtils.equals(accessToken.getToken(), accessTokenString)) {
            throw new VirgoRestException(RestExceptionFactor.INVALID_ACCESS_TOKEN);
        }

        if (accessToken.isExpire()) {
            throw new VirgoRestException(RestExceptionFactor.EXPIRED_ACCESS_TOKEN);
        }

        if (accessToken != null) {
            VirgoLogger.debug("parsed token, token:{}, user_id:{}, app_key:{}",
                    accessToken.getToken(), accessToken.getUserId(), accessToken.getAppKey());

            requestContext.getHeaders().add("from_user_id", Long.toString(accessToken.getUserId()));
            requestContext.getHeaders().add("app_key", "" + Integer.toString(accessToken.getAppKey()));
        }

        return accessToken;
    }
}
