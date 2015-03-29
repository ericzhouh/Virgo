package com.winterfarmer.virgo.restapi.core.param;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by yangtianhang on 15-1-9.
 */
public abstract class AbstractParamSpec<T> implements ParamDescriptor, ParamSpec<T> {
    @Override
    public String toString() {
        return getSpec();
    }

    public static String[] splitSpecNameAndValue(String spec) {
        String[] nameAndValue = StringUtils.split(spec, ":", 2);
        String name = StringUtils.lowerCase(StringUtils.trim(nameAndValue[0]));
        String value = nameAndValue.length < 2 ? "" : StringUtils.trim(nameAndValue[1]);
        return new String[]{name, value};
    }

    protected static String createSpec(String specName) {
        return createSpec(specName, null);
    }

    protected static String createSpec(String specName, String specValue) {
        if (StringUtils.isBlank(specValue)) {
            return specName;
        } else {
            return specName + ":" + specValue;
        }
    }
}
