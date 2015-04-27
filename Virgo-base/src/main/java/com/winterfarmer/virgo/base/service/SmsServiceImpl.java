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

    private static final String VERIFICATION_CODE_MSG = "感谢您注册XXX，您的注册码是%d，二十分钟内有效。";

    @Override
    public boolean sendSignUpMobileVerificationCode(String mobileNum, int code) {
        return sendSms(mobileNum, String.format(VERIFICATION_CODE_MSG, code));
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

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/x-www-form-urlencoded;charset=gbk");
        httpService.post(BAIWU_URL, nameValues, headers, "UTF-8");

        String result = httpService.postAsync(BAIWU_URL, nameValues);
        if (StringUtils.startsWith(result, "0#")) {
            return true;
        } else {
            VirgoLogger.error("baiwu sms error: " + result);
            return false;
        }
    }
}
