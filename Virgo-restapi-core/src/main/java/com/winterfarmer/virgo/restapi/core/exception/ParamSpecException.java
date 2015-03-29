package com.winterfarmer.virgo.restapi.core.exception;

/**
 * Created by yangtianhang on 15-1-9.
 */
public class ParamSpecException extends RuntimeException {
    public ParamSpecException(String message) {
        super(message);
    }

    public ParamSpecException(Class<?> clazz, Number min, Number max) {
        super("Invalid spec definition of " + clazz.getName() + " min: " + min + " max: " + max);
    }

    public ParamSpecException(Class<?> clazz, String specValue) {
        super("Invalid spec definition of " + clazz.getName() + " spec value: " + specValue);
    }
}
