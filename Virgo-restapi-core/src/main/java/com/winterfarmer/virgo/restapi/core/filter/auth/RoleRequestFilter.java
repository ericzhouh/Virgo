package com.winterfarmer.virgo.restapi.core.filter.auth;

import com.winterfarmer.virgo.account.model.AccessToken;
import com.winterfarmer.virgo.account.model.Role;
import com.winterfarmer.virgo.restapi.core.exception.RestExceptionFactor;
import com.winterfarmer.virgo.restapi.core.exception.VirgoRestException;
import com.winterfarmer.virgo.restapi.core.filter.FilterPriorities;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;
import java.util.List;

/**
 * Created by yangtianhang on 15-4-16.
 */
@Priority(FilterPriorities.ROLES)
public class RoleRequestFilter extends AccountFilter implements ContainerRequestFilter {
    private final List<Role> roleList;

    public RoleRequestFilter(List<Role> roleList) {
        super();
        this.roleList = roleList;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        AccessToken accessToken = getAccessTokenAndExplainToHeader(requestContext);
        if (!staffService.hasPrivilege(accessToken.getUserId(), roleList)) {
            throw new VirgoRestException(RestExceptionFactor.NO_RIGHTS);
        }
    }
}
