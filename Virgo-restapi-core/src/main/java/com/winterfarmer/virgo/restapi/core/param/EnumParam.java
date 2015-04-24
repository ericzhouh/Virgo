package com.winterfarmer.virgo.restapi.core.param;

import com.google.common.collect.Sets;
import com.winterfarmer.virgo.restapi.core.exception.ParamSpecException;
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

    public EnumParam(String specValue) {
        this(parseSpecValue(specValue));
    }

    private static int[] parseSpecValue(String specValue) {
        try {
            specValue = StringUtils.trim(specValue);
            checkParentheses(specValue);
            specValue = retrieveParentheses(specValue);
            Set<Integer> enumSet = getEnumSet(specValue);
            if (enumSet == null) {
                return null;
            }

            return ArrayUtils.toPrimitive(enumSet.toArray(new Integer[0]));
        } catch (Exception e) {
            throw new ParamSpecException("Invalid enum param spec: " + specValue);
        }
    }

    private static Set<Integer> getEnumSet(String specValue) {
        // 全集返回null
        if (StringUtils.trim(specValue).equalsIgnoreCase("U")) {
            return null;
        }

        String[] strings = specValue.split(",");
        Set<Integer> enumSet = Sets.newTreeSet();

        for (String string : strings) {
            if (isRange(string)) {
                Integer[] enumRange = parseEnumRange(string);
                for (int i = enumRange[0]; i <= enumRange[1]; ++i) {
                    enumSet.add(i);
                }
            } else {
                enumSet.add(Integer.parseInt(StringUtils.trim(string)));
            }
        }

        return enumSet;
    }

    private static String retrieveParentheses(String specValue) {
        return StringUtils.mid(specValue, 1, specValue.length() - 2);
    }

    private static void checkParentheses(String specValue) {
        if (!StringUtils.startsWith(specValue, "{")) {
            throw new RuntimeException();
        }

        if (!StringUtils.endsWith(specValue, "}")) {
            throw new RuntimeException();
        }
    }

    private static Integer[] parseEnumRange(String enumRangeStr) {
        String[] split = StringUtils.split(enumRangeStr, "~");
        Integer[] enumRange = new Integer[]
                {Integer.parseInt(StringUtils.trim(split[0])), Integer.parseInt(StringUtils.trim(split[0]))};
        if (enumRange[1] > enumRange[0]) {
            throw new RuntimeException();
        }

        return enumRange;
    }

    private static boolean isRange(String string) {
        return StringUtils.contains(string, '~');
    }

    public EnumParam(int[] enums) {
        if (enums == null) { // complete set
            this.specValue = "{U}";
        } else {
            for (int i : enums) {
                enumSet.add(i);
            }
            this.specValue = createSpecValue(enums);
        }

        this.spec = createSpec(getName(), getValue());
    }

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
        try {
            int val = Integer.parseInt(value);
            if (enumSet == null) {
                return true;
            } else {
                return enumSet.contains(val);
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Integer parse(String strParam) {
        return Integer.parseInt(strParam);
    }

    @Override
    public boolean isCompatible(Class<?> clazz) {
        return clazz == int.class || clazz == Integer.class;
    }

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

    public static String specName() {
        return "Enum";
    }

    private static String createSpecValue(int[] enums) {
        return "{" + StringUtils.join(enums, ",") + "}";
    }
}
