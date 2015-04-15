package com.winterfarmer.virgo.base.model;

import com.winterfarmer.virgo.common.util.EnumUtil;

/**
 * Created by yangtianhang on 15-2-26.
 */
public enum CommonState {
    DELETE("删除", 0),
    NORMAL("正常", 1),
    //
    ;

    private final String name;
    private final int index;

    private CommonState(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public static CommonState valueByIndex(int index) {
        for (CommonState a : CommonState.values()) {
            if (a.getIndex() == index) {
                return a;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return EnumUtil.toString(CommonState.class, this.name, this.index);
    }
}
