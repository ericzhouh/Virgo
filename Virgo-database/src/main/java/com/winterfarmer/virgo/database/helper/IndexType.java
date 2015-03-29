package com.winterfarmer.virgo.database.helper;

/**
 * Created by yangtianhang on 15-3-7.
 */
public enum IndexType {
    btree("btree"),
    hash("hash");

    private final String name;

    IndexType(String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }
}
