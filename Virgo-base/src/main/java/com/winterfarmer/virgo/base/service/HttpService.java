package com.winterfarmer.virgo.base.service;

import org.apache.commons.lang3.tuple.Pair;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by yangtianhang on 15-4-15.
 */
public interface HttpService {
    String get(String url);

    String getAsync(String url);

    String getAsync(final String url, final long timeout);

    Future<String> getAsyncFuture(String url);

    <T> T get(String url, ResultConvert<T> c);

    String get(String url, String charset);

    public String get(String url, Collection<Pair<String, Object>> paramCollection);

    byte[] getByte(String url);

    String post(String url, Map<String, ?> nameValues);

    String postAsync(String url, Map<String, ?> nameValues);

    String postAsync(String url, String jsonParam);

    Future<String> postAsyncFuture(String url, Map<String, ?> nameValues);

    <T> T post(String url, Map<String, ?> nameValues, ResultConvert<T> c);

    String post(String url, Map<String, ?> nameValues, String charset);

    String post(String url, Map<String, ?> nameValues, Map<String, String> headers, String charset);

    String post(String url, String jsonParam);

    <T> T post(String url, Map<String, ?> nameValues, String charset, ResultConvert<T> c);

    /**
     * Map<String, Object> nameValues value type support ByteArrayPart,FileItem,String
     *
     * @param url
     * @param nameValues
     * @param charset
     * @return
     */
    String postMulti(String url, Map<String, Object> nameValues, String charset);

    String postMulti(String url, Map<String, Object> nameValues);

    /**
     * 直接将inputstream 作为 body，发送到服务端
     *
     * @param url
     * @param in
     * @return
     */
    String postMulti(String url, InputStream in);

    /**
     * @param url
     * @param buf
     * @return
     * @see #postMulti(String, InputStream)
     */
    String postMulti(String url, byte[] buf);

    RequestBuilder buildGet(String url);

    RequestBuilder buildPost(String url);

    public interface ResultConvert<T> {
        T convert(String url, String post, String result);
    }

    void setAccessLog(AccessLog accessLog);

    public interface AccessLog {
        void accessLog(long time, String method, int status, int len, String url,
                       String post, String ret);
    }

    public interface RequestBuilder {
        /**
         * 设置请求的uri charset和content charset
         *
         * @param charset
         * @return
         */
        RequestBuilder withCharset(String charset);

        RequestBuilder withParam(Map<String, ?> param);

        RequestBuilder withParam(boolean condition, String key, Object value);

        RequestBuilder withParam(String key, Object value);

        RequestBuilder withHeader(Map<String, String> header);

        RequestBuilder withHeader(boolean condition, String key, String value);

        RequestBuilder withHeader(String key, String value);

        <T> T execute(ResultConvert<T> convert);

        String execute();

        Future<String> executeAsync();

        String executeAsyncString();

        byte[] executeByte();
    }
}
