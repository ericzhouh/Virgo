package com.winterfarmer.virgo.restapi.core.annotation;

import com.winterfarmer.virgo.restapi.core.exception.RestExceptionFactor;
import com.winterfarmer.virgo.restapi.core.exception.VirgoRestException;

/**
 * Created by yangtianhang on 15-1-24.
 */
public @interface RestApiInfo {
    String desc() default "";

    Protocol protocol() default Protocol.HTTPS;

    public AuthPolicy authPolicy() default AuthPolicy.PUBLIC;

    String[] cautions() default {};

    String[] extraParamDesc() default {};

    String[] errors() default {};

    public static enum Protocol {
        HTTP, HTTPS
    }

    public static enum AuthPolicy {
        PUBLIC {
            @Override
            public String desc() {
                return "公开";
            }
        }, OAUTH {
            @Override
            public String desc() {
                return "OAuth";
            }
        };

        public abstract String desc();
    }
}
