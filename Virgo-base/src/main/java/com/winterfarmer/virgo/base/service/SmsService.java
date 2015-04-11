package com.winterfarmer.virgo.base.service;

/**
 * Created by yangtianhang on 15-4-11.
 */
public interface SmsService {
    boolean sendSms(String mobileNum, String msg);

    boolean sendSignUpMobileVerificationCode(String mobileNum, int code);
}
