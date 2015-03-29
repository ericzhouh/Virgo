package com.winterfarmer.example.restapi;

import com.winterfarmer.virgo.restapi.core.exception.VirgoExceptionMapper;
import com.winterfarmer.virgo.restapi.core.feature.FastJsonFeature;
import com.winterfarmer.virgo.restapi.core.filter.BaseDynamicFilter;
import com.winterfarmer.virgo.restapi.core.filter.LogFilter;
import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

import javax.json.stream.JsonGenerator;
import javax.ws.rs.ApplicationPath;

/**
 * Created by yangtianhang on 15-1-4.
 */
@ApplicationPath("simple")
public class SimpleApiRegistration extends ResourceConfig {
    public SimpleApiRegistration() {
        packages("com.example");

//        register(org.glassfish.jersey.server.spring.SpringComponentProvider.class);
        register(MultiPartFeature.class);
        register(LogFilter.class);
        register(BaseDynamicFilter.class);
        register(VirgoExceptionMapper.class);
        register(JsonProcessingFeature.class).property(JsonGenerator.PRETTY_PRINTING, false);
        register(RequestContextFilter.class);
        register(FastJsonFeature.class);
    }
}
