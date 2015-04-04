package com.winterfarmer.virgo.restapi.core.filter;

import org.glassfish.jersey.server.model.Parameter;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import java.util.List;

/**
 * Created by yangtianhang on 15-1-22.
 * 因为每个resource method都不一样，这里是对每个method进行filter，所以使用动态binding
 */
public class ParamValidityDynamicFilter implements DynamicFeature {
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        List<Parameter> parameters = Parameter.create(
                resourceInfo.getResourceClass(),
                resourceInfo.getClass(),
                resourceInfo.getResourceMethod(),
                true);
        
        ParamValidityFilter paramValidityFilter = new ParamValidityFilter(parameters);
        context.register(paramValidityFilter);
    }
}
