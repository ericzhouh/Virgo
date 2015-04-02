package com.winterfarmer.virgo.restapi.v1;

import com.winterfarmer.virgo.log.VirgoLogger;
import com.winterfarmer.virgo.restapi.core.exception.VirgoExceptionMapper;
import com.winterfarmer.virgo.restapi.core.feature.FastJsonFeature;
import com.winterfarmer.virgo.restapi.core.filter.VirgoLogFilter;
import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.json.stream.JsonGenerator;
import javax.ws.rs.ApplicationPath;

/**
 * Created by yangtianhang on 15-3-30.
 */
@ApplicationPath("v1")
public class RestV1Application extends ResourceConfig {
    public RestV1Application() {
        VirgoLogger.info("init RestV1Application()");
        packages("com.winterfarmer.virgo.restapi.v1");
        register(VirgoLogFilter.class);
        register(VirgoExceptionMapper.class);
        register(JsonProcessingFeature.class).property(JsonGenerator.PRETTY_PRINTING, false);
        register(FastJsonFeature.class);
    }
}
