package com.winterfarmer.virgo.restapi.core.param;

import com.google.common.collect.Lists;
import com.winterfarmer.virgo.restapi.core.exception.RestExceptionFactor;
import com.winterfarmer.virgo.restapi.core.exception.VirgoRestException;
import com.winterfarmer.virgo.restapi.core.request.ContainerRequestUtil;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.server.ContainerRequest;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by yangtianhang on 15-1-10.
 */
public class ParamValidator {
    private final String paramName;
    private final boolean isRequired;
    private final Class<?> paramClass;
    private final Type paramType;
    private final String defaultValue;
    private final AbstractParamSpec<?> paramSpec;
    private final boolean isQueryParameter;

    private final String missingParamMsg;
    private final String invalidParamMsg;

    public ParamValidator(String paramName, boolean isRequired, Class<?> paramClass,
                          Type paramType, String defaultValue, String spec, boolean isQueryParameter)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.paramName = paramName;
        this.isRequired = isRequired;
        this.paramClass = paramClass;
        this.paramType = paramType;
        this.defaultValue = defaultValue;
        this.paramSpec = ParamSpecFactory.getParamSpec(spec);
        this.isQueryParameter = isQueryParameter;
        this.missingParamMsg = "Missing Parameter:" + this.paramName + ", spec:" + spec + "!";
        this.invalidParamMsg = "Invalid Parameter:" + this.paramName + ", spec:" + spec + "!";
    }

    public void validate(ContainerRequestContext request) {
        if (isQueryParameter) {
            validate(request.getUriInfo().getQueryParameters(), request.getUriInfo().getPath());
        } else {
            if (ContainerRequestUtil.isMultipartRequest(request)) {
                Map<String, BodyPart> bodyParts = ContainerRequestUtil.getMultipartParameters((ContainerRequest) request, true);
                validate(bodyParts, request.getUriInfo().getPath());
            } else {
                validate(ContainerRequestUtil.getFormParameters((ContainerRequest) request, true), request.getUriInfo().getPath());
            }
        }
    }

    private void validate(MultivaluedMap<String, String> params, String path) {
        List<String> values = params.get(paramName);
        if (isRequired && CollectionUtils.isEmpty(values)) {
            throw new VirgoRestException(RestExceptionFactor.MISSING_PARAM, missingParamMsg, path);
        } else if (CollectionUtils.isEmpty(values)) {
            return;
        }

        // TODO: ??? 这里如果不copy一次,会把一些需要校验的数据给修改掉,导致后续出错
        // List<String> newValues = Lists.newArrayList(values);
        // Collections.copy(newValues, values);
        validate(values, path);
    }

    private void validate(List<String> values, String path) {
        for (String value : values) {
            if (!paramSpec.isValid(value)) {
                throw new VirgoRestException(RestExceptionFactor.INVALID_PARAM, invalidParamMsg, path);
            }
        }
    }

    private void validate(Map<String, BodyPart> bodyParts, String path) {
        BodyPart bodyPart = bodyParts.get(this.paramName);
        if (bodyPart == null) {
            if (this.isRequired) {
                throw new VirgoRestException(RestExceptionFactor.MISSING_PARAM, missingParamMsg, path);
            } else {
                return;
            }
        }

        //TODO 暂时不支持文件校验，后续支持
        if (!"text".equalsIgnoreCase(bodyPart.getMediaType().getType())) {
            return;
        }

        Object entity = bodyPart.getEntity();
        if (entity == null && isRequired) {
            throw new VirgoRestException(RestExceptionFactor.MISSING_PARAM, missingParamMsg, path);
        }

        String value = bodyPart.getEntityAs(String.class);
        if (this.isRequired && value == null) {
            throw new VirgoRestException(RestExceptionFactor.MISSING_PARAM, missingParamMsg, path);
        }

        List<String> values = Lists.newArrayList(value.toString());
        if (CollectionUtils.isEmpty(values)) {
            return;
        }

        validate(values, path);
    }
}