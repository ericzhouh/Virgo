package com.winterfarmer.virgo.aggregator.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.winterfarmer.virgo.base.annotation.ApiField;
import com.winterfarmer.virgo.base.annotation.ApiMode;
import com.winterfarmer.virgo.knowledge.model.KnowledgeCounterType;

import java.util.Map;

import static com.winterfarmer.virgo.knowledge.model.KnowledgeCounterType.*;

/**
 * Created by yangtianhang on 15/6/8.
 */
@ApiMode(desc = "用户计数")
public class ApiUserCounter {
    @JSONField(name = "user_question_count")
    @ApiField(desc = "用户提问个数")
    private Integer userQuestionCount;

    @JSONField(name = "user_answered_count")
    @ApiField(desc = "用户回答个数")
    private Integer userAnsweredCount;

    @JSONField(name = "user_obtain_agree_count")
    @ApiField(desc = "用户获得的赞的个数")
    private Integer userObtainAgreeCount;

    @JSONField(name = "user_collect_count")
    @ApiField(desc = "用户收藏的答案个数")
    private Integer userCollectCount;

    @JSONField(name = "user_follow_count")
    @ApiField(desc = "用户关注的问题个数")
    private Integer userFollowCount;

    public ApiUserCounter() {
    }

    public ApiUserCounter(Map<KnowledgeCounterType, Integer> map) {
        for (Map.Entry<KnowledgeCounterType, Integer> entry : map.entrySet()) {
            switch (entry.getKey()) {
                case USER_QUESTION_COUNT:
                    setUserQuestionCount(entry.getValue());
                    break;
                case USER_ANSWERED_COUNT:
                    setUserAnsweredCount(entry.getValue());
                    break;
                case USER_OBTAIN_AGREE_COUNT:
                    setUserObtainAgreeCount(entry.getValue());
                    break;
                case USER_COLLECT_COUNT:
                    setUserCollectCount(entry.getValue());
                    break;
                case USER_FOLLOW_COUNT:
                    setUserFollowCount(entry.getValue());
                    break;
            }
        }
    }

    public Integer getUserFollowCount() {
        return userFollowCount;
    }

    public void setUserFollowCount(Integer userFollowCount) {
        this.userFollowCount = userFollowCount;
    }

    public Integer getUserQuestionCount() {
        return userQuestionCount;
    }

    public void setUserQuestionCount(Integer userQuestionCount) {
        this.userQuestionCount = userQuestionCount;
    }

    public Integer getUserAnsweredCount() {
        return userAnsweredCount;
    }

    public void setUserAnsweredCount(Integer userAnsweredCount) {
        this.userAnsweredCount = userAnsweredCount;
    }

    public Integer getUserObtainAgreeCount() {
        return userObtainAgreeCount;
    }

    public void setUserObtainAgreeCount(Integer userObtainAgreeCount) {
        this.userObtainAgreeCount = userObtainAgreeCount;
    }

    public Integer getUserCollectCount() {
        return userCollectCount;
    }

    public void setUserCollectCount(Integer userCollectCount) {
        this.userCollectCount = userCollectCount;
    }
}
