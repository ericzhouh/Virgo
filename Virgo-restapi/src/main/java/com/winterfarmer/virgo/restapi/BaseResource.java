package com.winterfarmer.virgo.restapi;

/**
 * Created by yangtianhang on 15-4-13.
 */
public abstract class BaseResource {
    // define common parameter specification

    protected static final String MOBILE_SPEC = "mobile";
    protected static final String PASSWORD_SPEC = "string:6~24";

    protected static final String APP_KEY_SPEC = "int:[1000,2000]";

    protected static final String VERIFICATION_CODE_SPEC = "int:[0,999999]";
}
