package com.winterfarmer.virgo.base.Exception;

/**
 * Created by yangtianhang on 15-4-11.
 */
public abstract class VirgoException extends Exception {
    public VirgoException(String msg) {
        super(msg);
    }

    public VirgoException() {
        super();
    }
}
