package com.example;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by yangtianhang on 15-2-4.
 */
public class TRest {
    @JSONField(name = "landmark_id")
    private long landmarkId;

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "score")
    private int score;

    private int not;

    public TRest(long landmarkId, String name, int score, int not) {
        this.landmarkId = landmarkId;
        this.name = name;
        this.score = score;
        this.not = not;
    }

    public long getLandmarkId() {
        return landmarkId;
    }

    public void setLandmarkId(long landmarkId) {
        this.landmarkId = landmarkId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getNot() {
        return not;
    }

    public void setNot(int not) {
        this.not = not;
    }
}
