package com.winterfarmer.virgo.restapi.core.exception;

import javax.ws.rs.WebApplicationException;

/**
 * Created by yangtianhang on 15-1-5.
 */
public final class VirgoRestException extends WebApplicationException {
    private static final long serialVersionUID = -3563785595073497082L;

    private final RestExceptionFactor factor;
    private final String reason;
    private final String uri;

    public VirgoRestException() {
        this(RestExceptionFactor.INTERNAL_SERVER_ERROR, "", "");
    }

    public VirgoRestException(RestExceptionFactor factor) {
        this(factor, "", "");
    }

    public VirgoRestException(RestExceptionFactor factor, String reason) {
        this(factor, reason, "");
    }

    public VirgoRestException(RestExceptionFactor factor, String reason, String uri) {
        this.factor = factor;
        this.reason = reason;
        this.uri = uri;
    }

    public RestExceptionFactor getFactor() {
        return factor;
    }

    public String getReason() {
        return reason;
    }

    public String getUri() {
        return uri;
    }
}
