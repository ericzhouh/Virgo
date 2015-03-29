package com.winterfarmer.virgo.database.helper;

/**
 * Created by yangtianhang on 15-3-7.
 */
public enum Collation {
    utf8mb4_unicode_ci("utf8mb4_unicode_ci");

    private final String name;

    Collation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
