package com.winterfarmer.virgo.restapi.core.custom;

import com.google.common.collect.Sets;
import com.winterfarmer.virgo.restapi.core.exception.ParamSpecException;
import com.winterfarmer.virgo.restapi.core.param.AbstractParamSpec;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * Created by yangtianhang on 15-1-9.
 */
public class EnumParam extends AbstractParamSpec<Integer> {
    private final Set<Integer> enumSet = Sets.newHashSet();
    private final String spec;
    private final String specValue;

    // 包括from，to
    public static String range(int from, int to) {
        int count = to - from + 1;
        if (count <= 0) {
            throw new ParamSpecException("Invalid enum param range, from " + from + " to: " + to);
        }

        int[] enums = new int[count];

        for (int i = 0; i < count; ++i) {
            enums[i] = from + i;
        }

        return new EnumParam(enums).getSpec();
    }

    public static String enumerate(int... enums) {
        if (ArrayUtils.isEmpty(enums)) {
            throw new ParamSpecException("Empty enum set");
        }

        return new EnumParam(enums).toString();
    }

    public EnumParam(int[] enums) {
        for (int i : enums) {
            enumSet.add(i);
        }

        this.specValue = createSpecValue(enums);
        this.spec = createSpec(getName(), getValue());
    }

    @Override
    public String description() {
        return "Enums: " + StringUtils.join(enumSet, ',');
    }

    @Override
    public String sample() {
        return Integer.toString(enumSet.iterator().next());
    }

    @Override
    public boolean isValid(String value) {
        return false;
    }

    @Override
    public Integer parse(String strParam) {
        return Integer.parseInt(strParam);
    }

    @Override
    public boolean isCompatible(Class<?> clazz) {
        return clazz == int.class || clazz == Integer.class;
    }

//    @Override
//    protected boolean isValidSpecValue(String specValue) {
//        specValue = StringUtils.trim(specValue);
//        if (StringUtils.isEmpty(specValue)) {
//            return false;
//        }
//
//        if (StringUtils.startsWith(specValue, "{")) {
//            return false;
//        }
//
//        if (StringUtils.endsWith(specValue, "}")) {
//            return false;
//        }
//
//        specValue = StringUtils.mid(specValue, 1, specValue.length() - 2);
//        String[] strings = specValue.split(",");
//        for (String s : strings) {
//            try {
//                Integer.parseInt(s);
//            } catch (Exception e) {
//                return false;
//            }
//
//        }
//
//        return true;
//    }


    @Override
    public String getSpec() {
        return spec;
    }

    @Override
    public String getValue() {
        return specValue;
    }

    @Override
    public String getName() {
        return "Enum";
    }

    private static String createSpecValue(int[] enums) {
        return "{" + StringUtils.join(enums, ",") + "}";
    }
}
