package com.winterfarmer.virgo.restapi.core.filter.auth;

import com.winterfarmer.virgo.restapi.core.filter.FilterPriorities;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;

/**
 * Created by yangtianhang on 15-4-16.
 */
@Priority(FilterPriorities.AUTHENTICATION)
public class InternalRequestFilter extends AccountFilter implements ContainerRequestFilter {
    public InternalRequestFilter() {
        super();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

    }
}
