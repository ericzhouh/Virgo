package com.winterfarmer.virgo.database.helper;

/**
 * Created by yangtianhang on 15-3-7.
 */
public enum Charset {
    utf8mb4("utf8mb4");

    private final String name;

    Charset(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
