package com.winterfarmer.virgo.restapi.core.exception;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by yangtianhang on 15-1-5.
 */
public class ApiException {
    @JSONField(name = "request_uri")
    private String requestUri;

    @JSONField(name = "error_code")
    private int errorCode;

    @JSONField(name = "error_msg")
    private String reason;

    public ApiException(String requestUri, int errorCode, String reason) {
        this.requestUri = requestUri;
        this.errorCode = errorCode;
        this.reason = reason;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getReason() {
        return reason;
    }
}
