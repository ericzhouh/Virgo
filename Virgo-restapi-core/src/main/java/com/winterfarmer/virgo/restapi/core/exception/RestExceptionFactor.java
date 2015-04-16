package com.winterfarmer.virgo.restapi.core.exception;

/**
 * Created by yangtianhang on 15-1-5.
 */
public enum RestExceptionFactor {
    // General Exception Factor
    NOT_FOUND(404, 404_000_000, "not found"),
    INVALID_PARAM(400, 400_000_001, "invalid parameter"),
    MISSING_PARAM(400, 400_000_002, "missing parameter"),

    // Privilege Exception Factor
    MISSING_ACCESS_TOKEN(403, 403_000_000, "missing access token"),
    INVALID_ACCESS_TOKEN(403, 403_000_001, "invalid access token"),
    EXPIRED_ACCESS_TOKEN(403, 403_000_002, "expired access token"),
    NO_RIGHTS(403, 403_000_003, "no rights"),

    // Account Exception Factor
    INVALID_MOBILE_NUMBER(404, 404_001_000, "invalid mobile number"),
    REQUEST_SIGN_UP_MOBILE_VERIFICATION_CODE_TOO_FREQUENTLY(404, 404_001_001, "repeat sign up mobile verification code too frequently"),
    INVALID_MOBILE_VERIFICATION_CODE(404, 404_001_002, "invalid mobile verification code"),
    INVALID_ID_OR_PASSWORD(404, 404_001_003, "invalid id or password"),
    RESET_PASSWORD_FAILED(404, 404_001_004, "reset password failed"),
    MOBILE_NUMBER_HAS_BEEN_REGISTERED(404, 404_001_005, "mobile number has been registered"),

    // Vehicle Exception Factor
    VEHICLE_NOT_EXISTED(404, 404_002_000, "vehicle not existed"),

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
