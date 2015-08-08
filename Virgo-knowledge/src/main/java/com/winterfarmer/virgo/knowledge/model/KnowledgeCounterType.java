package com.winterfarmer.virgo.knowledge.model;

import com.winterfarmer.virgo.common.util.EnumUtil;

import java.util.Map;

/**
 * Created by yangtianhang on 15/6/5.
 */
public enum KnowledgeCounterType implements EnumUtil.VirgoEnum {
    USER_QUESTION_COUNT("用户提问个数", 0x01),
    USER_ANSWERED_COUNT("用户回答个数", 0x02),
    USER_OBTAIN_AGREE_COUNT("用户获得的赞的个数", 0x03),
    USER_COLLECT_COUNT("用户收藏的答案个数", 0x04), // 不存在counter里，由graphdb获得
    USER_FOLLOW_COUNT("用户关注的问题个数", 0x05), // 不存在counter里，由graphdb获得

    QUESTION_ANSWERED_COUNT("问题的回答个数", 0x11),
    ANSWER_COMMENT_COUNT("回答的评论个数", 0x12);

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
