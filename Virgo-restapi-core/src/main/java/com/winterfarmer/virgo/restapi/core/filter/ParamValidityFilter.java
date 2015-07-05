package com.winterfarmer.virgo.restapi.core.filter;

import com.google.common.collect.Lists;
import com.winterfarmer.virgo.log.VirgoLogger;
import com.winterfarmer.virgo.restapi.core.annotation.ParamSpec;
import com.winterfarmer.virgo.restapi.core.param.ParamValidator;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.model.Parameter;

import javax.annotation.Priority;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Created by yangtianhang on 15-1-4.
 */
@Priority(FilterPriorities.PARAMETER_VALIDATOR)
public class ParamValidityFilter implements ContainerRequestFilter {
    private final List<ParamValidator> validators;

    public ParamValidityFilter(List<Parameter> parameters) {
        if (parameters.isEmpty()) {
            validators = Collections.emptyList();
        } else {
            validators = Lists.newArrayListWithCapacity(parameters.size());
            this.initValidators(parameters);
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        VirgoLogger.debug("being ParamValidityFilter");
        for (ParamValidator validator : this.validators) {
            validator.validate(requestContext);
        }
        VirgoLogger.debug("end ParamValidityFilter");
    }

    private void initValidators(List<Parameter> parameters) {
        for (Parameter parameter : parameters) {
            addValidator(parameter);
        }
    }

    private void addValidator(Parameter parameter) {
        if (!isCheckingParam(parameter)) {
            return;
        }

        ParamSpec desc = parameter.getAnnotation(ParamSpec.class);
        if (desc == null) {
            return;
        }

        Class<?> parameterClass = parameter.getRawType();
        // TODO: 过滤掉附件格式, 暂时不支持
        if (parameterClass == FormDataBodyPart.class) {
            return;
        }

        String defaultValue = parameter.getDefaultValue();
        boolean isRequired = isReallyRequired(desc.isRequired(), parameterClass.isPrimitive(), defaultValue != null);

        try {
            Type parameterType = parameter.getType();
            String range = desc.spec();

            ParamValidator validator = new ParamValidator(
                    parameter.getSourceName(), isRequired, parameterClass,
                    parameterType, defaultValue, range, parameter.getAnnotation(QueryParam.class) != null);
            this.validators.add(validator);
        } catch (Exception e) {
            VirgoLogger.fatal("Add validator failed, param name: " + parameter.getSourceName(), e);
        }
    }

    private boolean isReallyRequired(boolean isRequired, boolean isPrimitiveParam, boolean hasDefaultValue) {
        return isRequired && isPrimitiveParam && !hasDefaultValue;
    }

    private boolean isCheckingParam(Parameter parameter) {
        return parameter.getAnnotation(QueryParam.class) != null ||
                parameter.getAnnotation(FormParam.class) != null ||
                parameter.getAnnotation(FormDataParam.class) != null;
    }
}