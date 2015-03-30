package com.winterfarmer.rest.simple;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * Created by yangtianhang on 15-3-29.
 */
@ApplicationPath("/rest")
public class MyApplication extends ResourceConfig {
    public MyApplication() {
        System.out.println("-----------------MyApplication()-----------------");
        packages("com.winterfarmer.rest.simple.example");
    }
}
