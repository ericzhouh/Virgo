package com.winterfarmer.virgo.restapi.core.annotation;

import com.winterfarmer.virgo.account.model.RolePrivilege;
import com.winterfarmer.virgo.account.model.GroupType;
import com.winterfarmer.virgo.restapi.core.exception.RestExceptionFactor;

/**
 * Created by yangtianhang on 15-1-24.
 */
public @interface RestApiInfo {
    public String desc() default "";

    public Protocol protocol() default Protocol.HTTPS;

    public AuthPolicy authPolicy() default AuthPolicy.PUBLIC;

    public String[] cautions() default {};

    public String[] extraParamDesc() default {};

    public RestExceptionFactor[] errors() default {};

    public GroupType groupType() default GroupType.PUBLIC;

    public RolePrivilege[] rolePrivileges() default {RolePrivilege.VIEW};

    public static enum Protocol {
        HTTP, HTTPS
    }

    // 授权策略
    public static enum AuthPolicy {
        PUBLIC {
            @Override
            public String desc() {
                return "公开";
            }
        },

        OAUTH {
            @Override
            public String desc() {
                return "OAuth";
            }
        },

        INTERNAL {
            @Override
            public String desc() {
                return "Internal";
            }
        },;

        public abstract String desc();
    }
}
