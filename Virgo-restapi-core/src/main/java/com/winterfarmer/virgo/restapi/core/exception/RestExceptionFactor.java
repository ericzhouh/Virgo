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
