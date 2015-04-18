package com.winterfarmer.virgo.restapi.core.filter.auth;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.winterfarmer.virgo.account.model.Role;
import com.winterfarmer.virgo.account.model.RolePrivilege;
import com.winterfarmer.virgo.log.VirgoLogger;
import com.winterfarmer.virgo.restapi.core.annotation.RestApiInfo;
import org.glassfish.jersey.server.model.AnnotatedMethod;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import java.util.List;
import java.util.Set;

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
            registerAccountFilter(authPolicy, context);
            registerRoleFilter(restApiInfo, context);
        }
    }

    private void registerAccountFilter(AuthPolicy authPolicy, FeatureContext context) {
        context.register(getAccountFilter(authPolicy));
    }

    private void registerRoleFilter(RestApiInfo restApiInfo, FeatureContext context) {
        if (restApiInfo.authPolicy() == AuthPolicy.INTERNAL) {
            List<Role> roleList = Lists.newArrayList();
            Set<RolePrivilege> rolePrivileges = Sets.newHashSet(restApiInfo.rolePrivileges());
            for (RolePrivilege rolePrivilege : rolePrivileges) {
                roleList.add(new Role(restApiInfo.groupType(), rolePrivilege));
            }

            context.register(new RoleRequestFilter(roleList));
        }
    }

    private AccountFilter getAccountFilter(AuthPolicy authPolicy) {
        switch (authPolicy) {
            case OAUTH:
                return new OAuthRequestFilter();
            case INTERNAL:
                return new InternalRequestFilter();
            case PUBLIC:
                return new PublicRequestFilter();
            default:
                VirgoLogger.warn("no auth policy use default oauth");
                return new OAuthRequestFilter();
        }
    }
}
