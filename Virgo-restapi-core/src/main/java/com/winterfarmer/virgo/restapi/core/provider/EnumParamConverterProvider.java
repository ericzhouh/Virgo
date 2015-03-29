package com.winterfarmer.virgo.restapi.core.provider;

import com.winterfarmer.virgo.log.VirgoLogger;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.server.internal.inject.ExtractorException;

import javax.inject.Singleton;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;

@Provider
@Singleton
public class EnumParamConverterProvider implements ParamConverterProvider {
    @Override
    public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType, final Annotation[] annotations) {
        return (!Enum.class.isAssignableFrom(rawType)) ? null : doConverter(rawType);
    }

    private <T> ParamConverter<T> doConverter(final Class<T> rawType) {
        final Method fromStringMethod = AccessController.doPrivileged(ReflectionHelper.getFromStringStringMethodPA(rawType));
        final Method valueByIndexMethod = AccessController.doPrivileged(EnumParamConverterProvider.getValueByIndexStringMethodPA(rawType));

        AbstractStringReader<T> returnConverter = null;
        if (fromStringMethod != null) {
            returnConverter = new AbstractStringReader<T>() {
                @Override
                public T _fromString(final String value) throws Exception {
                    return rawType.cast(fromStringMethod.invoke(null, value));
                }
            };
        } else if (valueByIndexMethod != null) {
            returnConverter = new AbstractStringReader<T>() {
                @Override
                public T _fromString(final String value) throws Exception {
                    if (!StringUtils.isNumeric(value)) {
                        return null;
                    }

                    return rawType.cast(valueByIndexMethod.invoke(null, Integer.parseInt(value)));
                }
            };
        }

        return returnConverter;
    }

    private static abstract class AbstractStringReader<T> implements ParamConverter<T> {
        @Override
        public T fromString(final String value) {
            try {
                return _fromString(value);
            } catch (final InvocationTargetException ex) {
                if (value.isEmpty()) {
                    return null;
                }

                final Throwable cause = ex.getCause();
                if (cause instanceof WebApplicationException) {
                    throw (WebApplicationException) cause;
                } else {
                    throw new ExtractorException(cause);
                }
            } catch (final Exception ex) {
                throw new ProcessingException(ex);
            }
        }

        protected abstract T _fromString(String value) throws Exception;

        @Override
        public String toString(final T value) throws IllegalArgumentException {
            return value.toString();
        }

    }

    public static PrivilegedAction<Method> getValueByIndexStringMethodPA(final Class<?> clazz) {
        return new PrivilegedAction<Method>() {
            @Override
            public Method run() {
                try {
                    final Method m = clazz.getDeclaredMethod("valueByIndex", Integer.TYPE);
                    if (!Modifier.isStatic(m.getModifiers()) && m.getReturnType() == clazz) {
                        VirgoLogger.error("Invalid converter for class:{}", clazz.getName());
                        return null;
                    }

                    return m;
                } catch (NoSuchMethodException e) {
                    VirgoLogger.error("No converter for class:{}", clazz.getName());
                    return null;
                }
            }
        };
    }
}
