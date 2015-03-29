package com.winterfarmer.virgo.restapi.core.param;

import com.winterfarmer.virgo.restapi.core.exception.ParamSpecException;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by yangtianhang on 15-1-9.
 */
abstract class NumericParam<T extends Number> extends AbstractParamSpec<T> {
    final protected T min;
    final protected T max;
    final protected boolean isMinOpen;
    final protected boolean isMaxOpen;

    final private String specValue;
    final private String spec;
    final private String description;

    static final private String LEFT_OPEN_BRACKET = "(";
    static final private String LEFT_CLOSED_BRACKET = "[";
    static final private String RIGHT_OPEN_BRACKET = ")";
    static final private String RIGHT_CLOSED_BRACKET = "]";

    protected NumericParam(String specValue) {
        try {
            specValue = StringUtils.trim(specValue);

            this.isMinOpen = isLeftBracketOpen(specValue);
            this.isMaxOpen = isRightBracketOpen(specValue);

            String[] strings = StringUtils.mid(specValue, 1, specValue.length() - 2).split(",");
            this.min = parseNumber(StringUtils.trim(strings[0]));
            this.max = parseNumber(StringUtils.trim(strings[1]));

            if (compare(min, max) > 0) {
                throw new RuntimeException();
            }

            this.specValue = createSpecValue(min, isMinOpen, max, isMaxOpen);
            this.spec = createSpec(getName(), getValue());
            this.description = createDescription(getName(), getValue());
        } catch (Exception e) {
            throw new ParamSpecException(this.getClass(), specValue);
        }
    }

    protected NumericParam(T min, boolean isMinOpen, T max, boolean isMaxOpen) {
        if (min.doubleValue() > max.doubleValue()) {
            throw new ParamSpecException(this.getClass(), min, max);
        }

        this.min = min;
        this.max = max;
        this.isMinOpen = isMinOpen;
        this.isMaxOpen = isMaxOpen;

        this.specValue = createSpecValue(min, isMinOpen, max, isMaxOpen);
        this.spec = createSpec(getName(), getValue());
        this.description = createDescription(getName(), getValue());
    }

    @Override
    public String getValue() {
        return specValue;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public String getSpec() {
        return spec;
    }

    private static <T> String createSpecValue(T min, boolean isMinOpen, T max, boolean isMaxOpen) {
        String leftBracket = isMinOpen ? LEFT_OPEN_BRACKET : LEFT_CLOSED_BRACKET;
        String rightBracket = isMaxOpen ? RIGHT_OPEN_BRACKET : RIGHT_CLOSED_BRACKET;

        return leftBracket + min + ", " + max + rightBracket;
    }

    private static String createDescription(String numberType, String specValue) {
        return "Number: " + numberType + ", range: " + specValue;
    }

    private boolean isLeftBracketOpen(String specValue) {
        String leftBracket = StringUtils.left(specValue, 1);
        if (leftBracket.equals(LEFT_OPEN_BRACKET)) {
            return true;
        } else if (leftBracket.equals(LEFT_CLOSED_BRACKET)) {
            return false;
        } else {
            throw new RuntimeException();
        }
    }

    private boolean isRightBracketOpen(String specValue) {
        String rightBracket = StringUtils.right(specValue, 1);
        if (rightBracket.equals(RIGHT_OPEN_BRACKET)) {
            return true;
        } else if (rightBracket.equals(RIGHT_CLOSED_BRACKET)) {
            return false;
        } else {
            throw new RuntimeException();
        }
    }

    abstract protected T parseNumber(String number);

    abstract protected int compare(T a, T b);



}
