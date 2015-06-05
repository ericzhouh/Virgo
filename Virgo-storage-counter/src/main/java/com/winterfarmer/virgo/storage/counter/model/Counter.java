package com.winterfarmer.virgo.storage.counter.model;

/**
 * Created by yangtianhang on 15/6/4.
 */
public class Counter {
    private long id;
    private int count;
    private int type;

    public Counter() {
    }

    public Counter(long id, int count, int type) {
        this.id = id;
        this.count = count;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
