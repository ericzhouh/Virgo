package com.winterfarmer.virgo.restapi.core.exception;

import com.alibaba.fastjson.JSON;
import com.winterfarmer.virgo.log.VirgoLogger;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.server.ParamException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Created by yangtianhang on 15-1-5.
 */
public class VirgoExceptionMapper implements ExceptionMapper<Throwable> {
    @Context
    private HttpServletRequest request;

    @Override
    public Response toResponse(Throwable throwable) {
        Response response = null;
        try {
            if (throwable instanceof WebApplicationException) {
                if (throwable instanceof VirgoRestException) {
                    response = buildVirgoRestExceptionResponse((VirgoRestException) throwable);
                } else if (throwable instanceof ParamException) {
                    response = buildParamExceptionResponse((ParamException) throwable);
                } else if (throwable instanceof NotAllowedException) {
                    response = buildResponseFromExceptionFactor(RestExceptionFactor.MISSING_PARAM);
                } else if (throwable instanceof NotFoundException) {
                    response = buildResponseFromExceptionFactor(RestExceptionFactor.NOT_FOUND);
                }

                if (response == null) {
                    response = buildInternalError();
                }
            } else {
                response = buildInternalError();
            }
        } catch (Throwable t1) {
            response = buildInternalError();
            // ApiLogger.error(t, "Severe error occured while building error response : %s", t1.getMessage());
        } finally {
            // log exception
            if (throwable instanceof VirgoRestException) {
                if (((VirgoRestException) throwable).getFactor() == RestExceptionFactor.INTERNAL_SERVER_ERROR) {
                    // ApiLogger.error("Throwable caught:%s", ((VirgoRestException) t).getExFactor().getError_msg());
                }
            } else {
                if (throwable instanceof ParamException) {
                } else if (throwable instanceof NotAllowedException) {
                } else if (throwable instanceof NotFoundException) {
                } else {
                    // ApiLogger.error(t, "Throwable caught: %s ", t);
                }
            }

            return response;
        }
    }


    private Response buildInternalError() {
        VirgoLogger.error("buildInternalError");
        return buildResponseFromExceptionFactor(RestExceptionFactor.INTERNAL_SERVER_ERROR);
    }

    private Response buildVirgoRestExceptionResponse(VirgoRestException exception) {
        return buildResponseFromExceptionFactor(exception.getFactor(), exception.getReason());
    }

    private Response buildResponseFromExceptionFactor(RestExceptionFactor factor) {
        return buildResponse(factor.getHttpErrorCode(), factor.getInnerErrorCode(), factor.getReason());
    }

    private Response buildResponseFromExceptionFactor(RestExceptionFactor factor, String errorMsg) {
        if (StringUtils.isBlank(errorMsg)) {
            errorMsg = factor.getReason();
        }

        return buildResponse(factor.getHttpErrorCode(), factor.getInnerErrorCode(), errorMsg);
    }

    private Response buildParamExceptionResponse(ParamException exception) {
        String paramName = exception.getParameterName();
        String paramType = exception.getParameterType().getCanonicalName();
        String errorMsg = "parameter: {" + paramName + "} should be compatible with Java type: {" + paramType + "}";

        return buildResponseFromExceptionFactor(RestExceptionFactor.INVALID_PARAM, errorMsg);
    }

    private Response buildResponse(int httpErrorCode, int innerErrorCode, String errorMsg) {
        ApiException apiException = new ApiException(request.getPathInfo(), innerErrorCode, errorMsg);
        String entity = JSON.toJSONString(apiException);
        return Response.status(httpErrorCode).type(MediaType.APPLICATION_JSON).entity(entity).build();
    }
}
