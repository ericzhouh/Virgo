package com.winterfarmer.virgo.restapi.core.param;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.winterfarmer.virgo.log.VirgoLogger;

/**
 * Created by yangtianhang on 15-1-9.
 */
public class MobileParam extends AbstractParamSpec<String> {
    static PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    static final MobileParam mobileParam = new MobileParam();

    private final String spec;
    private final String specValue;

    public static String mobile() {
        return mobileParam.getSpec();
    }

    public MobileParam(String dumy) {
        this();
    }

    private MobileParam() {
        this.specValue = "";
        this.spec = createSpec(getName());
    }

    @Override
    public String description() {
        return "mobile phone number";
    }

    @Override
    public String sample() {
        return "13269937228";
    }

    @Override
    public boolean isValid(String value) {
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(value, "CN");
            return phoneNumberUtil.isValidNumber(phoneNumber);
        } catch (NumberParseException e) {
            VirgoLogger.debug("Invalid phone number: " + value);
            return false;
        }
    }

    @Override
    public String parse(String strParam) {
        return strParam;
    }

    @Override
    public boolean isCompatible(Class<?> clazz) {
        return clazz == long.class || clazz == Long.class || clazz == String.class;
    }

//    @Override
//    protected boolean isValidSpecValue(String specValue) {
//        return StringUtils.trim(specValue).equals("#");
//    }

    @Override
    public String getName() {
        return "Mobile";
    }

    @Override
    public String getSpec() {
        return spec;
    }

    @Override
    public String getValue() {
        return specValue;
    }

    public static String specName() {
        return "Mobile";
    }
}
