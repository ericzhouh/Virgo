package com.winterfarmer.virgo.restapi.core.filter;

import javax.annotation.Priority;
import javax.ws.rs.container.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Created by yangtianhang on 15-4-4.
 */
@PreMatching
@Priority(FilterPriorities.CHARACTER_ENCODING)
public class CharacterEncodingFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        MediaType type = responseContext.getMediaType();
        if (type == null) {
            type = MediaType.APPLICATION_JSON_TYPE;
        }

        responseContext.getHeaders().remove(CONTENT_TYPE_HEADER);
        responseContext.getHeaders().add(CONTENT_TYPE_HEADER, type.toString() + ";charset=UTF-8");
    }
}
