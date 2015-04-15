package com.winterfarmer.virgo.base.service;

import com.google.common.collect.Maps;
import com.winterfarmer.virgo.common.util.AccountUtil;
import com.winterfarmer.virgo.log.VirgoLogger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by yangtianhang on 15-4-11.
 */
@Service("smsService")
public class SmsServiceImpl implements SmsService {
    private static final String BAIWU_URL = "http://service1.hbsmservice.com:8080/sms_send2.do"; // 百悟短信接口url

    @Resource(name = "httpService")
    HttpService httpService;

    @Override
    public boolean sendSms(String mobileNum, String msg) {
        return sendBaiwuSms(mobileNum, msg);
    }

    @Override
    public boolean sendSignUpMobileVerificationCode(String mobileNum, int code) {
        return false;
    }

    private boolean sendBaiwuSms(String mobileNum, String msg) {
        VirgoLogger.info("baiwu send [" + mobileNum + "]:" + msg);
        mobileNum = AccountUtil.removeMobileNationCode(mobileNum);
        Map<String, String> nameValues = Maps.newHashMap();
        nameValues.put("corp_id", "1f1m002");
        nameValues.put("corp_pwd", "xqm002");
        nameValues.put("corp_service", "1069yd");
        nameValues.put("mobile", mobileNum);
        nameValues.put("msg_content", msg);

        String result = httpService.postAsync(BAIWU_URL, nameValues);
        System.out.println(result);
        return StringUtils.equals(result, "0#1%");
    }
}
