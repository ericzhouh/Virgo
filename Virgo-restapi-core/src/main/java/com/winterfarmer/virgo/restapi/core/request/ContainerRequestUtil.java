package com.winterfarmer.virgo.restapi.core.request;

import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.message.internal.MediaTypes;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.Encoded;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * Created by yangtianhang on 15-1-7.
 */
public class ContainerRequestUtil {
    private final static Annotation encodedAnnotation = getEncodedAnnotation();

    private static Annotation getEncodedAnnotation() {
        /**
         * Encoded-annotated class.
         */
        @Encoded
        final class EncodedAnnotationTemp {
        }

        return EncodedAnnotationTemp.class.getAnnotation(Encoded.class);
    }

    public static boolean isMultipartRequest(ContainerRequestContext request) {
        return MediaTypes.typeEqual(MediaType.MULTIPART_FORM_DATA_TYPE, request.getMediaType());
    }

    public static MultivaluedMap<String, String> getParameters(ContainerRequestContext requestContext) {
        if (HttpMethod.GET.equalsIgnoreCase(requestContext.getMethod())) {
            return requestContext.getUriInfo().getQueryParameters();
        }

        if (ContainerRequestUtil.isMultipartRequest(requestContext)) {
            Map<String, BodyPart> bodyParts = ContainerRequestUtil.getMultipartParameters((ContainerRequest) requestContext, true);
            return covertBodyPartToParamsPart(bodyParts);
        }

        return getFormParameters((ContainerRequest) requestContext, true);
    }

    public static Map<String, BodyPart> getMultipartParameters(ContainerRequest request, boolean decode) {
        if (MediaTypes.typeEqual(MediaType.MULTIPART_FORM_DATA_TYPE, request.getMediaType())) {
            request.bufferEntity();
            FormDataMultiPart form;

            if (decode) {
                form = request.readEntity(FormDataMultiPart.class);
            } else {
                Annotation[] annotations = ArrayUtils.toArray(encodedAnnotation);
                form = request.readEntity(FormDataMultiPart.class, annotations);
            }

            return toBodyPartMap(form.getBodyParts());
        }

        return null;
    }

    private static Map<String, BodyPart> toBodyPartMap(List<BodyPart> bodyParts) {
        Map<String, BodyPart> maps = Maps.newHashMap();
        for (BodyPart bodyPart : bodyParts) {
            Map<String, String> parameters = bodyPart.getContentDisposition().getParameters();
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                maps.put(entry.getValue(), bodyPart);
            }
        }

        return maps;
    }

    public static MultivaluedMap<String, String> getFormParameters(ContainerRequest request, boolean decode) {
        Form form = null;

        if (MediaTypes.typeEqual(MediaType.APPLICATION_FORM_URLENCODED_TYPE, request.getMediaType())) {
            request.bufferEntity();
            if (decode) {
                form = request.readEntity(Form.class);
            } else {
                Annotation[] annotations = ArrayUtils.toArray(encodedAnnotation);
                form = request.readEntity(Form.class, annotations);
            }
        }

        return (form == null ? new Form().asMap() : form.asMap());
    }

    private static String getMultipartRequestParam(ContainerRequest request, String key) {
        Map<String, BodyPart> bodyPartMap = getMultipartParameters(request, true);
        if (MapUtils.isEmpty(bodyPartMap)) {
            return null;
        }

        BodyPart bodyPart = bodyPartMap.get(key);
        if (bodyPart == null) {
            return null;
        }

        try {
            String paramValue = bodyPart.getEntityAs(String.class);
            if (StringUtils.isNotBlank(paramValue)) {
                return paramValue.trim();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String getParam(ContainerRequestContext requestContext, String key) {
        String paramValue;

        if (isMultipartRequest(requestContext)) {
            return getMultipartRequestParam((ContainerRequest) requestContext.getRequest(), key);
        }

        MultivaluedMap<String, String> multivaluedMap;

        if (HttpMethod.GET.equalsIgnoreCase(requestContext.getMethod())) {
            multivaluedMap = requestContext.getUriInfo().getQueryParameters();
        } else {
            multivaluedMap = getFormParameters((ContainerRequest) requestContext.getRequest(), true);
        }

        if (MapUtils.isEmpty(multivaluedMap)) {
            return null;
        }

        List<String> values = multivaluedMap.get(key);
        if (CollectionUtils.isEmpty(values)) {
            return null;
        }

        paramValue = values.get(0);
        if (StringUtils.isNotBlank(paramValue)) {
            return paramValue.trim();
        }

        return null;
    }

    public static String getHeaderParam(ContainerRequestContext requestContext, String key) {
        MultivaluedMap<String, String> map = requestContext.getHeaders();

        if (map != null) {
            List<String> values = map.get(key);
            if (CollectionUtils.isNotEmpty(values)) {
                // 系统约定同一个请求中的头部不会给同一个key赋多个值
                return values.get(0);
            }
        }

        return null;
    }

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ACCESS_TOKEN_KEY = "access_token";

    public static String getAccessToken(ContainerRequestContext requestContext) {
        String authorizationString = requestContext.getHeaderString(AUTHORIZATION_HEADER);

        if (StringUtils.isNotBlank(authorizationString)) {
            return authorizationString.trim();
        } else {
            return getParam(requestContext, ACCESS_TOKEN_KEY);
        }
    }


    public static Map<String, String> getParametersMap(ContainerRequestContext requestContext) {
        return toMap(getParameters(requestContext));
    }

    private static Map<String, String> toMap(MultivaluedMap<String, String> parameters) {
        Map<String, String> map = Maps.newHashMap();
        for (String key : parameters.keySet()) {
            map.put(key, parameters.getFirst(key));
        }

        return map;
    }

    private static MultivaluedMap<String, String> covertBodyPartToParamsPart(Map<String, BodyPart> bodyParts) {
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();
        for (Map.Entry<String, BodyPart> entry : bodyParts.entrySet()) {
            BodyPart bodyPart = entry.getValue();
            if (!"text".equalsIgnoreCase(bodyPart.getMediaType().getType())) {
                result.putSingle("multipart_file", bodyPart.getContentDisposition().getFileName() + "," + bodyPart.getContentDisposition().getSize());
                continue;
            }

            String value = bodyPart.getEntityAs(String.class);
            result.putSingle(entry.getKey(), value);
        }

        return result;
    }
}
