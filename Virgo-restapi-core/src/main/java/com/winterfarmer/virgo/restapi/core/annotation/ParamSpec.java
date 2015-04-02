package com.winterfarmer.virgo.restapi.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yangtianhang on 15-1-10.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ParamSpec {
    public boolean isRequired();

    public String desc() default "";

    public String spec() default "";
}
