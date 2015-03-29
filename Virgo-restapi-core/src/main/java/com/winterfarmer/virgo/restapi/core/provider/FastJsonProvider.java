package com.winterfarmer.virgo.restapi.core.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * Created by yangtianhang on 15-1-25.
 */
@Provider
@Consumes({MediaType.APPLICATION_JSON, "text/json"})
@Produces({MediaType.APPLICATION_JSON, "text/json"})
public class FastJsonProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object> {
    private final static Charset UTF8 = Charset.forName("UTF-8");
    private final static Charset charset = UTF8;
    private static final SerializerFeature[] features = new SerializerFeature[]{
            SerializerFeature.WriteNullBooleanAsFalse, SerializerFeature.WriteNullStringAsEmpty
            , SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullNumberAsZero
            , SerializerFeature.SkipTransientField, SerializerFeature.DisableCircularReferenceDetect
    };

    private static final String JSON_ORIG = "json";
    private static final String PLUS_JSON = "+json";

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Object.class.isAssignableFrom(type) && supportsMediaType(mediaType);
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        byte[] inputBytes = IOUtils.toByteArray(entityStream, 1024);
        IOUtils.closeQuietly(entityStream);

        return JSON.parseObject(inputBytes, 0, inputBytes.length, charset.newDecoder(), genericType);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Object.class.isAssignableFrom(type) && supportsMediaType(mediaType);
    }

    @Override
    public long getSize(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        String text = o == null ? "{}" : JSON.toJSONString(o, features);
        byte[] bytes = text.getBytes(charset);
        entityStream.write(bytes);
    }

    /**
     * @return true for all media types of the pattern *&#47;json and
     * *&#47;*+json.
     */
    private boolean supportsMediaType(final MediaType mediaType) {
        return mediaType.getSubtype().equals(JSON_ORIG) || mediaType.getSubtype().endsWith(PLUS_JSON);
    }
}
