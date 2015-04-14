package com.winterfarmer.virgo.restapi;

import com.alibaba.fastjson.JSON;
import com.winterfarmer.virgo.common.model.BaseModel;

/**
 * Created by yangtianhang on 15-4-13.
 */
public abstract class BaseResource {
    // define common parameter specification

    protected static final String HEADER_USER_ID = "from_user_id";
    protected static final String HEADER_APP_KEY = "app_key";

    protected static final String NORMAL_PAGE_SPEC = "int:[0,10000]";
    protected static final String NORMAL_COUNT_SPEC = "int:[0,50]";

    protected static final String NORMAL_PAGE_DESC = "页码, 从0开始";
    protected static final String NORMAL_COUNT_DESC = "每页个数";

    protected static final String COMMON_STATE_SPEC = "enum:{0,1}";

    protected static final String NORMAL_LONG_ID_SPEC = "long:[1,9999999999]";
    protected static final String EXTENSION_SPEC = "string:0~32768";

    protected static final String MOBILE_SPEC = "mobile";
    protected static final String PASSWORD_SPEC = "string:6~24";

    protected static final String APP_KEY_SPEC = "int:[1000,2000]";

    protected static final String VERIFICATION_CODE_SPEC = "int:[0,999999]";

    protected static void setProperties(BaseModel model, String extension) {
        if (extension != null) {
            model.setProperties(JSON.parseObject(extension));
        }
    }
}
