package com.winterfarmer.virgo.restapi.core.annotation;

import com.winterfarmer.virgo.account.model.GroupType;
import com.winterfarmer.virgo.account.model.RolePrivilege;
import com.winterfarmer.virgo.base.model.CommonResult;
import com.winterfarmer.virgo.restapi.core.exception.RestExceptionFactor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yangtianhang on 15-1-24.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RestApiInfo {
    String desc() default "";

    Protocol protocol() default Protocol.HTTPS;

    AuthPolicy authPolicy() default AuthPolicy.PUBLIC;

    String[] cautions() default {};

    String[] extraParamDesc() default {};

    RestExceptionFactor[] errors() default {};

    GroupType groupType() default GroupType.PUBLIC;

    RolePrivilege[] rolePrivileges() default {RolePrivilege.VIEW};

    Class resultDemo() default CommonResult.class;

    enum Protocol {
        HTTP, HTTPS
    }

    // 授权策略
    enum AuthPolicy {
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
