package com.winterfarmer.virgo.knowledge.model;

import com.winterfarmer.virgo.common.util.EnumUtil;

import java.util.Map;

/**
 * Created by yangtianhang on 15/6/5.
 */
public enum KnowledgeCounterType implements EnumUtil.VirgoEnum {
    USER_QUESTION_COUNT("用户提问个数", 1),
    USER_ANSWERED_COUNT("用户回答个数", 2),
    QUESTION_ANSWERED_COUNT("问题的回答个数", 3);

    private final String name;
    private final int index;

    private static final Map<Integer, KnowledgeCounterType> enumMap = EnumUtil.getEnumMap(KnowledgeCounterType.class);

    KnowledgeCounterType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public static KnowledgeCounterType valueByIndex(int index) {
        return enumMap.get(index);
    }

    @Override
    public String toString() {
        return EnumUtil.toString(this);
    }
}
