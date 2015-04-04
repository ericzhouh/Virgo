package com.winterfarmer.virgo.restapi.core.filter.auth;

import com.winterfarmer.virgo.restapi.core.annotation.RestApiInfo;
import org.glassfish.jersey.server.model.AnnotatedMethod;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import static com.winterfarmer.virgo.restapi.core.annotation.RestApiInfo.*;

/**
 * Created by yangtianhang on 15-3-29.
 */
public class AuthDynamicFilter implements DynamicFeature {
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        AnnotatedMethod annotatedMethod = new AnnotatedMethod(resourceInfo.getResourceMethod());

        if (annotatedMethod.isAnnotationPresent(RestApiInfo.class)) {
            RestApiInfo restApiInfo = annotatedMethod.getAnnotation(RestApiInfo.class);
            AuthPolicy authPolicy = restApiInfo.authPolicy();
            switch (authPolicy) {
                case OAUTH:
                    context.register(new OAuthDynamicFilter());
                    return;
                case PUBLIC:
                default:
                    return;
            }
        }
    }
}
