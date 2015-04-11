package com.winterfarmer.virgo.base.service;

import com.winterfarmer.virgo.log.VirgoLogger;
import org.springframework.stereotype.Service;

/**
 * Created by yangtianhang on 15-4-11.
 */
@Service("smsService")
public class SmsServiceImpl implements SmsService {
    @Override
    public boolean sendSms(String mobileNum, String msg) {
        VirgoLogger.info("send [" + mobileNum + "]:" + msg);
        return false;
    }

    @Override
    public boolean sendSignUpMobileVerificationCode(String mobileNum, int code) {
        return false;
    }
}
