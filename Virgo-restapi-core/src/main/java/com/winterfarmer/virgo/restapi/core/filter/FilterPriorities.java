package com.winterfarmer.virgo.restapi.core.filter;

/**
 * Created by yangtianhang on 15-1-10.
 */
public class FilterPriorities {
    public static final int REWRITE = 100;
    public static final int CHARACTER_ENCODING = 400;
    public static final int LOGGER = 500;

    /**
     * Security authentication filter/interceptor priority.
     */
    public static final int AUTHENTICATION = 1_000;

    public static final int ROLES = 1_500;

    /**
     * Security authorization filter/interceptor priority.
     */
    public static final int AUTHORIZATION = 2_000;
    /**
     * Header decorator filter/interceptor priority.
     */
    public static final int HEADER_DECORATOR = 3_000;
    /**
     * Message encoder or decoder filter/interceptor priority.
     */
    public static final int ENTITY_CODER = 4_000;
    /**
     * User-level filter/interceptor priority.
     */
    public static final int USER = 5_000;

    public static final int PARAMETER_VALIDATOR = 6_000;
}
