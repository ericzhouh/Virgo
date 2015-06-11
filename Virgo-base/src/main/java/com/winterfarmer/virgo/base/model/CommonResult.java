package com.winterfarmer.virgo.base.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by yangtianhang on 15-4-11.
 */
public class CommonResult extends HashMap<String, Object> implements Serializable {
    private static final long serialVersionUID = -6761372715880704145L;

    public static final String IS_SUCCESSFUL_KEY = "is_successful";

    public static CommonResult isSuccessfulCommonResult(boolean isSuccessful) {
        CommonResult commonResult = new CommonResult();
        commonResult.put(IS_SUCCESSFUL_KEY, isSuccessful);
        return commonResult;
    }

    public static CommonResult oneResultCommonResult(String key, Object value) {
        CommonResult commonResult = new CommonResult();
        commonResult.put(key, value);
        return commonResult;
    }

    public static CommonResult newCommonResult(String key0, Object value0,
                                               String key1, Object value1) {
        CommonResult commonResult = new CommonResult();
        commonResult.put(key0, value0);
        commonResult.put(key1, value1);
        return commonResult;
    }

    public static CommonResult newCommonResult(String key0, Object value0,
                                               String key1, Object value1,
                                               String key2, Object value2) {
        CommonResult commonResult = new CommonResult();
        commonResult.put(key0, value0);
        commonResult.put(key1, value1);
        commonResult.put(key2, value2);
        return commonResult;
    }
}
