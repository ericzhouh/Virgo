package com.winterfarmer.virgo.aggregator.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.winterfarmer.virgo.account.model.ExpertApplying;
import com.winterfarmer.virgo.account.model.UserInfo;
import com.winterfarmer.virgo.base.annotation.ApiField;
import com.winterfarmer.virgo.base.annotation.ApiMode;
import com.winterfarmer.virgo.base.util.StaticFileUtil;
import com.winterfarmer.virgo.knowledge.model.QuestionTag;

import java.util.List;

/**
 * Created by yangtianhang on 15-4-18.
 */
@ApiMode(desc = "用户信息")
public class ApiUser {
    @JSONField(name = "user_id")
    @ApiField(desc = "用户id")
    private long userId;

    @JSONField(name = "nick_name")
    @ApiField(desc = "昵称")
    private String nickName;

    @JSONField(name = "portrait")
    @ApiField(desc = "头像")
    private String portrait;

    @JSONField(name = "gender")
    @ApiField(desc = "性别 {0,1}->{M,F}")
    private int gender;

    @JSONField(name = "birthday")
    @ApiField(desc = "生日 1970-01-01")
    private String birthday;

    @JSONField(name = "real_name")
    @ApiField(desc = "真实姓名")
    private String realName;

    @JSONField(name = "email")
    @ApiField(desc = "email")
    private String email;

    @JSONField(name = "introduction")
    @ApiField(desc = "自我介绍")
    private String introduction;

    @JSONField(name = "user_type")
    @ApiField(desc = "用户类型 {0,1}->{普通,专家}")
    private int userType;

    //==============专家相关============
    @JSONField(name = "expert_tag")
    @ApiField(desc = "擅长领域的标签")
    private List<ApiQuestionTag> expertTags;

    //==============以下是申请专家相关============
    @JSONField(name = "applying_expert_reason")
    @ApiField(desc = "申请成为专家的理由")
    private String reason;

    @JSONField(name = "applying_expert_time")
    @ApiField(desc = "申请成为专家的时间")
    private Long applyingTime;

    @JSONField(name = "applying_expert_state")
    @ApiField(desc = "申请成为专家的状态: {0,1,2} -> {申请中,通过,拒绝}")
    private Integer applyingState;

    @JSONField(name = "applying_expert_tag")
    @ApiField(desc = "申请的擅长领域的标签")
    private List<ApiQuestionTag> applyingExpertTags;

    public static ApiUser simpleUser(UserInfo userInfo) {
        ApiUser apiUser = new ApiUser();
        apiUser.setNickName(userInfo.getNickName());
        apiUser.setPortrait(StaticFileUtil.getPortraitUrl(userInfo.getPortrait()));
        apiUser.setUserId(userInfo.getUserId());
        apiUser.setUserType(userInfo.getUserType().getIndex());

        return apiUser;
    }

    private ApiUser() {
    }

    public ApiUser(long userId, int userType) {
        this.userId = userId;
        this.userType = userType;
    }

    public ApiUser(UserInfo userInfo) {
        this.userId = userInfo.getUserId();
        this.nickName = userInfo.getNickName();
        this.portrait = StaticFileUtil.getPortraitUrl(userInfo.getPortrait());
        this.gender = userInfo.getGender();
        this.birthday = userInfo.getBirthday().toString();
        this.realName = userInfo.getRealName();
        this.email = userInfo.getEmail();
        this.introduction = userInfo.getIntroduction();
        this.userType = userInfo.getUserType().getIndex();
    }

    public ApiUser(UserInfo userInfo, ExpertApplying applying, QuestionTag[] tags) {
        this(userInfo);
        reason = applying.getReason();
        applyingTime = applying.getApplyingTime();
        applyingState = applying.getState();
        applyingExpertTags = ApiQuestionTag.from(tags);
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getApplyingTime() {
        return applyingTime;
    }

    public void setApplyingTime(Long applyingTime) {
        this.applyingTime = applyingTime;
    }

    public Integer getApplyingState() {
        return applyingState;
    }

    public void setApplyingState(Integer applyingState) {
        this.applyingState = applyingState;
    }

    public List<ApiQuestionTag> getApplyingExpertTags() {
        return applyingExpertTags;
    }

    public void setApplyingExpertTags(List<ApiQuestionTag> applyingExpertTags) {
        this.applyingExpertTags = applyingExpertTags;
    }

    public List<ApiQuestionTag> getExpertTags() {
        return expertTags;
    }

    public void setExpertTags(List<ApiQuestionTag> expertTags) {
        this.expertTags = expertTags;
    }
}
