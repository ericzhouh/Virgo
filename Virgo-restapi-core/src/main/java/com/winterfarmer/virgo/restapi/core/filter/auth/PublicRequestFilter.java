package com.winterfarmer.virgo.restapi.core.filter.auth;

import com.winterfarmer.virgo.restapi.core.filter.FilterPriorities;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;

/**
 * Created by yangtianhang on 15-4-16.
 */
@Priority(FilterPriorities.AUTHENTICATION)
public class PublicRequestFilter extends AccountFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (StringUtils.isBlank(getAccessTokenString(requestContext))) {
            getAccessTokenAndExplainToHeader(requestContext);
        }
    }
}
