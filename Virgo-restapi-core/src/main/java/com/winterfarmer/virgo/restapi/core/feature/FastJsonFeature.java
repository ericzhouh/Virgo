package com.winterfarmer.virgo.restapi.core.feature;

import com.winterfarmer.virgo.restapi.core.provider.FastJsonProvider;
import org.glassfish.jersey.CommonProperties;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * Created by yangtianhang on 15-1-25.
 */
public class FastJsonFeature implements Feature {
    @Override
    public boolean configure(FeatureContext context) {
//        final String disableMoxy = CommonProperties.MOXY_JSON_FEATURE_DISABLE + '.' +
//                context.getConfiguration().getRuntimeType().name().toLowerCase();
//        context.property(disableMoxy, true);
        context.property(CommonProperties.MOXY_JSON_FEATURE_DISABLE, true);
        context.register(FastJsonProvider.class, MessageBodyReader.class, MessageBodyWriter.class);
        return true;
    }
}
