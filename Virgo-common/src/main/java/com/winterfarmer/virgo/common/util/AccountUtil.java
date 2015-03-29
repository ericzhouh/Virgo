package com.winterfarmer.virgo.common.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * Created by yangtianhang on 15-3-27.
 */
public class AccountUtil {
    static PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public static String formatMobile(String mobile) {
        boolean mobileValid;
        Phonenumber.PhoneNumber phoneNumber;
        try {
            phoneNumber = phoneNumberUtil.parse(mobile, "CN");
            mobileValid = phoneNumberUtil.isValidNumber(phoneNumber);
        } catch (NumberParseException e) {
            throw new RuntimeException("invalid mobile number: " + mobile);
        }

        if (!mobileValid || phoneNumber == null) {
            throw new RuntimeException("invalid mobile number: " + mobile);
        }

        String formatPhoneNumber = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        return formatPhoneNumber;
    }

    public static void main(String[] args) {
        System.out.println(formatMobile("+8613269037228"));
    }
}
