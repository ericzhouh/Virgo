package com.winterfarmer.virgo.restapi.core.provider;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * TODO
 * 因为jersey会对list,set,sortset处理参见 MultivaluedParameterExtractorFactory ,
 * 而我们这种不标准的用,拼接字符串的方案是一个很山寨的方法,我又不想对jersey的源码进行修改,
 * 所以我们约定,所有使用这种,拼接的方式的接口,
 * 如果要使用range为spec:join_xxx那么在接口里对这个参数的定义就应该定义为Collection<T> 的样式,
 * 这样我们的框架会自动对参数进行split, 组合成一个Collection(我们会使用list来实现),
 * 同时会实现参数校验
 * 如果你写了这个range而又没有按要求定义参数, 那请你自己做参数校验以及对参数进行分解
 */
@Provider
public class JoinParamConverterProvider implements ParamConverterProvider {
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        return null;
    }
    //
//    @Override
//    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
//        for (Annotation annotation : annotations) {
//            if (annotation instanceof ParamDesc) {
//                ParamDesc paramDesc = (ParamDesc) annotation;
//                String range = paramDesc.range();
//                if (range.startsWith("spec:join_")) {
//                    if (Collection.class.isAssignableFrom(rawType)) {
//                        return doConverter(rawType, genericType, annotations);
//                    } else {
//                        ApiLogger.error("ParamDesc is {},range is {} ,but param value is not Collection ", paramDesc, range);
//                    }
//
//                }
//            }
//        }
//        return null;
//    }
//
//    private <T> ParamConverter<T> doConverter(final Class<T> rawType, final Type genericType, final Annotation[] annotations) {
////        final List<ClassTypePair> ctps = ReflectionHelper.getTypeArgumentAndClass(rawType);
////        ClassTypePair ctp = (ctps.size() == 1) ? ctps.get(0) : null;
////
////        System.out.println(ctp.rawClass());
//        return new AbstractStringReader<T>() {
//            @Override
//            public T _fromString(final String value) throws Exception {
//                Iterable<String> values = Splitter.on(",").split(value);
//                return (T) value;
//            }
//        };
//    }
//
//
//    private static abstract class AbstractStringReader<T> implements ParamConverter<T> {
//
//        ParamConverter converter;
//
//        protected AbstractStringReader(ParamConverter converter) {
//            this.converter = converter;
//        }
//
//        protected AbstractStringReader() {
//        }
//
//        @Override
//        public T fromString(final String value) {
//            try {
//                return _fromString(value);
//            } catch (final InvocationTargetException ex) {
//                // if the value is an empty string, return null
//                if (value.isEmpty()) {
//                    return null;
//                }
//                final Throwable cause = ex.getCause();
//                if (cause instanceof WebApplicationException) {
//                    throw (WebApplicationException) cause;
//                } else {
//                    throw new ExtractorException(cause);
//                }
//            } catch (final Exception ex) {
//                throw new ProcessingException(ex);
//            }
//        }
//
//        protected abstract T _fromString(String value) throws Exception;
//
//        @Override
//        public String toString(final T value) throws IllegalArgumentException {
//            return value.toString();
//        }
//    }


}
