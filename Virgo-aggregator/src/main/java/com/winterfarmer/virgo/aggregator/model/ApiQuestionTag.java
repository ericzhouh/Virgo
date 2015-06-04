package com.winterfarmer.virgo.aggregator.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;
import com.winterfarmer.virgo.base.annotation.ApiField;
import com.winterfarmer.virgo.base.annotation.ApiMode;
import com.winterfarmer.virgo.knowledge.model.QuestionTag;

import java.util.List;

/**
 * Created by yangtianhang on 15/5/15.
 */
@ApiMode(desc = "问题标签")
public class ApiQuestionTag {
    @JSONField(name = "tag_id")
    @ApiField(desc = "标签id")
    private long id;

    @JSONField(name = "tag_name")
    @ApiField(desc = "标签名称")
    private String name;

    @JSONField(name = "weight")
    @ApiField(desc = "热度")
    private int weight;

    public static List<ApiQuestionTag> from(QuestionTag[] questionTags) {
        List<ApiQuestionTag> apiQuestionTagList = Lists.newArrayList();
        for (QuestionTag questionTag : questionTags) {
            apiQuestionTagList.add(new ApiQuestionTag(questionTag));
        }

        return apiQuestionTagList;
    }

    public ApiQuestionTag(QuestionTag questionTag) {
        this.id = questionTag.getId();
        this.name = questionTag.getName();
        this.weight = questionTag.getWeight();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
