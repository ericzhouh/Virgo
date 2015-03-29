package com.winterfarmer.virgo.restapi.core.param;

/**
 * Created by yangtianhang on 15-1-9.
 * <p/>
 * spec形式:
 * SpecName:SpecValue
 */
public interface ParamSpec<T> {
    boolean isValid(String value);

    boolean isCompatible(Class<?> clazz);

    T parse(String s);

    String getSpec();

    String getName();

    String getValue();
}
