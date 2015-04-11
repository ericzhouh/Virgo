package com.winterfarmer.virgo.common.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.commons.codec.binary.Base32;

import java.util.Arrays;

/**
 * Created by yangtianhang on 15-3-27.
 */
public class AccountUtil {
    static PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    private static final int MOBILE_SECRET_SIZE = 15;
    private static final int INTERVAL = 30;
    private static final int PASS_CODE_LENGTH = 6;
    private static final String CRYPTO = "HmacSHA1";

    public static String formatMobile(String mobile) {
        boolean mobileValid;
        Phonenumber.PhoneNumber phoneNumber;
        try {
            phoneNumber = phoneNumberUtil.parse(mobile, "CN");
            mobileValid = phoneNumberUtil.isValidNumber(phoneNumber);
        } catch (NumberParseException e) {
            return null;
        }

        if (!mobileValid || phoneNumber == null) {
            return null;
        }

        String formatPhoneNumber = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        return formatPhoneNumber;
    }

    public static String generateMobileSecret(String mobile) {
        String formatMobile = String.format("%15s", mobile).replace(" ", "0");
        byte[] buffer = formatMobile.getBytes();
        return generateBaseSecret(buffer, MOBILE_SECRET_SIZE);
    }

    private static String generateBaseSecret(byte[] buffer, int secretSize) {
        Base32 codec = new Base32();
        byte[] secretKey = Arrays.copyOf(buffer, secretSize);
        byte[] encodedKey = codec.encode(secretKey);
        return new String(encodedKey);
    }

    public static boolean checkMobileCode(String mobilePhone, int code) {
        String secret = generateMobileSecret(mobilePhone);
        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(secret);
        int window = 20; //3分钟
        long currentInterval = getCurrentInterval();
        for (int i = -window; i <= window; ++i) {
            int hash = TOTPUtil.generateTOTP(decodedKey, currentInterval + i, PASS_CODE_LENGTH, CRYPTO);
            if (hash == code) {
                return true;
            }
        }
        return false;
    }

    public static int generateMobileCode(String mobilePhone) {
        String secret = generateMobileSecret(mobilePhone);
        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(secret);
        long currentInterval = getCurrentInterval();
        return TOTPUtil.generateTOTP(decodedKey, currentInterval, PASS_CODE_LENGTH, CRYPTO);
    }

    private static long getCurrentInterval() {
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        return currentTimeSeconds / INTERVAL;
    }

    public static void main(String[] args) {

        System.out.println(generateMobileSecret("13269937228"));
        System.out.println(formatMobile("+8613269037228"));
    }
}
