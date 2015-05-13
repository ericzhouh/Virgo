package com.winterfarmer.virgo.base.model;

/**
 * Created by yangtianhang on 15/5/13.
 */
abstract public class BaseIdModel extends BaseModel {
    private long id;

    protected BaseIdModel() {
    }

    protected BaseIdModel(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
