package com.winterfarmer.virgo.restapi.core.filter.auth;

import com.winterfarmer.virgo.account.model.AccessToken;
import com.winterfarmer.virgo.restapi.core.filter.FilterPriorities;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;

/**
 * Created by yangtianhang on 15-3-29.
 */
@Priority(FilterPriorities.AUTHENTICATION)
public class OAuthDynamicFilter extends AccountFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        AccessToken accessToken = getAccessTokenAndExplainToHeader(requestContext);
    }
}
