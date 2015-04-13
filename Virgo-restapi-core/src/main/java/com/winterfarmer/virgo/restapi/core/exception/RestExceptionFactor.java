package com.winterfarmer.virgo.restapi.core.exception;

/**
 * Created by yangtianhang on 15-1-5.
 */
public enum RestExceptionFactor {
    // General Exception Factor
    NOT_FOUND(404, 404_000_000, "not found"),
    INVALID_PARAM(400, 400_000_001, "invalid parameter"),
    MISSING_PARAM(400, 400_000_002, "missing parameter"),

    // ACCOUNT Exception Factor
    MISSING_ACCESS_TOKEN(403, 4030000, "missing access token"),
    INVALID_ACCESS_TOKEN(403, 4030001, "invalid access token"),
    EXPIRED_ACCESS_TOKEN(403, 4030002, "expired access token"),
    INVALID_MOBILE_NUMBER(404, 4040003, "invalid mobile number"),
    REQUEST_SIGN_UP_MOBILE_VERIFICATION_CODE_TOO_FREQUENTLY(404, 4040004, "repeat sign up mobile verification code too frequently"),
    INVALID_MOBILE_VERIFICATION_CODE(404, 4040005, "invalid mobile verification code"),
    INVALID_ID_OR_PASSWORD(404, 4040006, "invalid id or password"),
    RESET_PASSWORD_FAILED(404, 4040007, "reset password failed"),
    MOBILE_NUMBER_HAS_BEEN_REGISTERED(404, 4040007, "mobile number has been registered"),

    INTERNAL_SERVER_ERROR(500, 500_000_001, "Internal Server Error"),;

    private final int httpErrorCode;
    private final int innerErrorCode;
    private final String reason;

    RestExceptionFactor(int httpErrorCode, int innerErrorCode, String reason) {
        this.httpErrorCode = httpErrorCode;
        this.innerErrorCode = innerErrorCode;
        this.reason = reason;
    }

    public int getHttpErrorCode() {
        return httpErrorCode;
    }

    public int getInnerErrorCode() {
        return innerErrorCode;
    }

    public String getReason() {
        return reason;
    }
}
