package com.winterfarmer.virgo.restapi.v1;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * Created by yangtianhang on 15-3-30.
 */
@ApplicationPath("v1")
public class RestV1Application extends ResourceConfig {
    public RestV1Application() {
        System.out.println("=============RestV1Application==================");
        packages("com.winterfarmer.virgo.restapi.v1");
    }
}
