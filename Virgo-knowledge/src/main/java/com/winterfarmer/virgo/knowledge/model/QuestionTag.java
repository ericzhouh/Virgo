package com.winterfarmer.virgo.knowledge.model;

import com.winterfarmer.virgo.base.model.CommonState;

/**
 * Created by yangtianhang on 15/5/11.
 */
public class QuestionTag {
    private long id;
    private String name;
    private int weight;
    private CommonState commonState;

    public QuestionTag() {
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

    public CommonState getCommonState() {
        return commonState;
    }

    public void setCommonState(CommonState commonState) {
        this.commonState = commonState;
    }
}
