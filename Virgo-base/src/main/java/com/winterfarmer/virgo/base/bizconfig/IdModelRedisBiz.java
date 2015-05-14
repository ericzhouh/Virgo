package com.winterfarmer.virgo.base.bizconfig;

import com.winterfarmer.virgo.base.model.BaseIdModel;

/**
 * Created by yangtianhang on 15/5/14.
 */
public enum IdModelRedisBiz {
    toy(ToyIdModel.class, "toy");

    static class ToyIdModel extends BaseIdModel {
    }

    private final Class<? extends BaseIdModel> clazz;
    private final String bizName;
    private final String keyInfix;

    IdModelRedisBiz(Class<? extends BaseIdModel> clazz, String keyInfix) {
        this.clazz = clazz;
        this.bizName = clazz.getSimpleName();
        this.keyInfix = keyInfix;
    }

    public Class<? extends BaseIdModel> getClazz() {
        return clazz;
    }

    public String getBizName() {
        return bizName;
    }

    public String getKeyInfix() {
        return keyInfix;
    }
}
