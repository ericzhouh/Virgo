package com.winterfarmer.virgo.account.model;

import com.winterfarmer.virgo.storage.base.BaseModel;

/**
 * Created by yangtianhang on 15/6/3.
 */
public class ExpertApplying extends BaseModel {
    private static final long serialVersionUID = -1219696857124957191L;

    public static final int APPLYING = 0;
    public static final int PASS = 1;
    public static final int REJECT = 2;

    private long userId;
    private String reason;
    private long applyingTime;
    private int state; // {0,1,2} -> {申请中,通过,拒绝}

    public ExpertApplying() {
    }

    public ExpertApplying(long userId, String reason, long applyingTime, int state) {
        this.userId = userId;
        this.reason = reason;
        this.applyingTime = applyingTime;
        this.state = state;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getApplyingTime() {
        return applyingTime;
    }

    public void setApplyingTime(long applyingTime) {
        this.applyingTime = applyingTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
