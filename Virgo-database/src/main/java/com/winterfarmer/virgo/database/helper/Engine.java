package com.winterfarmer.virgo.database.helper;

/**
 * Created by yangtianhang on 15-3-7.
 */
public enum Engine {
    InnoDB("InnoDB");

    private final String name;

    Engine(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
