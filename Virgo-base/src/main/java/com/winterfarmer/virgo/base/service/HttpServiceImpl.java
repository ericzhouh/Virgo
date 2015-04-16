package com.winterfarmer.virgo.base.service;

import com.winterfarmer.virgo.common.thread.StandardThreadExecutor;
import com.winterfarmer.virgo.common.util.TextUtil;
import com.winterfarmer.virgo.log.VirgoLogger;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

/**
 * Created by yangtianhang on 15-4-15.
 */
public class HttpServiceImpl implements HttpService {
    private MultiThreadedHttpConnectionManager connectionManager;

    private HttpClient client;
    private int maxSize;
    private int soTimeOut;

    private ExecutorService httpPool;

    public HttpServiceImpl(int maxConnPerHost, int connTimeOutMs, int soTimeOutMs, int maxSize) {
        this(maxConnPerHost, connTimeOutMs, soTimeOutMs, maxSize, 1, 300);
    }

    public HttpServiceImpl(int maxConnPerHost, int connTimeOutMs, int soTimeOutMs, int maxSize, int minThread, int maxThread) {
        connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = connectionManager.getParams();
        params.setMaxTotalConnections(600);//这个值要小于tomcat线程池是800
        params.setDefaultMaxConnectionsPerHost(maxConnPerHost);
        params.setConnectionTimeout(connTimeOutMs);
        params.setSoTimeout(soTimeOutMs);
        this.soTimeOut = soTimeOutMs;

        HttpClientParams clientParams = new HttpClientParams();
        //忽略cookie 避免 Cookie rejected 警告
        clientParams.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
        client = new org.apache.commons.httpclient.HttpClient(clientParams, connectionManager);
        this.maxSize = maxSize;
        httpPool = new StandardThreadExecutor(minThread, maxThread);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                httpPool.shutdown();
                connectionManager.shutdown();
            }
        }));
    }

    private final static String DEFAULT_CHARSET = "utf-8";
    private AccessLog accessLog = new DefaultHttpServiceLog();
    private static final URLCodec urlCodec = new URLCodec("utf-8");

    public void setAccessLog(AccessLog accessLog) {
        this.accessLog = accessLog;
    }

    @Override
    public String get(String url) {
        return get(url, DEFAULT_CHARSET);
    }

    public String get(String url, Map<String, String> headers) {
        return get(url, headers, DEFAULT_CHARSET);
    }

    public String get(String url, Collection<Pair<String, Object>> paramCollection) {
        if (HttpManager.isBlockResource(url)) {
            VirgoLogger.httpDebug("getURL blockResource url={}", url);
            return "";
        }
        HttpMethod get = new GetMethod(url);
        HttpMethodParams params = new HttpMethodParams();
        params.setContentCharset(DEFAULT_CHARSET);
        params.setUriCharset(DEFAULT_CHARSET);
        for (Pair<String, Object> pair : paramCollection) {
            params.setParameter(pair.getKey(), pair.getValue());
        }

        get.setParams(params);
        addHeader(get, null);

        return executeMethod(url, get, null, DEFAULT_CHARSET);
    }

    @Override
    public String get(String url, String charset) {
        return get(url, null, charset);
    }

    public String get(String url, Map<String, String> headers, String charset) {
        if (HttpManager.isBlockResource(url)) {
            VirgoLogger.httpDebug("getURL blockResource url={}", url);
            return "";
        }
        HttpMethod get = new GetMethod(url);
        HttpMethodParams params = new HttpMethodParams();
        params.setContentCharset(charset);
        params.setUriCharset(charset);
        get.setParams(params);
        addHeader(get, headers);
        return executeMethod(url, get, null, charset);
    }

    public String getAsync(final String url) {
        return getAsync(url, soTimeOut);
    }

    public String getAsync(final String url, final long timeout) {
        Future<String> future = httpPool.submit(new Callable<String>() {
            public String call() throws Exception {

                return get(url);
            }
        });
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            VirgoLogger.httpWarn("getAsync error url:" + url + " msg:" + e.getMessage());
            return "";
        }
    }

    public Future<String> getAsyncFuture(final String url) {
        Future<String> future = httpPool.submit(new Callable<String>() {
            public String call() throws Exception {

                return get(url);
            }
        });
        return future;
    }

    public String postAsync(final String url, final Map<String, ?> nameValues) {
        Future<String> future = httpPool.submit(new Callable<String>() {
            public String call() throws Exception {
                return post(url, nameValues);
            }
        });
        try {
            return future.get(this.soTimeOut, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            VirgoLogger.httpWarn("getAsync error url:%s post:%s msg:%s", url, mapToString(nameValues), e.getMessage());
            return "";
        }
    }

    @Override
    public String postAsync(final String url, final String jsonParam) {
        Future<String> future = httpPool.submit(new Callable<String>() {
            public String call() throws Exception {
                return post(url, jsonParam);
            }
        });
        try {
            return future.get(this.soTimeOut, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            VirgoLogger.httpWarn("getAsync error url:%s post:%s msg:%s", url, jsonParam, e.getMessage());
            return "";
        }
    }


    public Future<String> postAsyncFuture(final String url, final Map<String, ?> nameValues) {
        Future<String> future = httpPool.submit(new Callable<String>() {
            public String call() throws Exception {
                return post(url, nameValues);
            }
        });
        return future;
    }

    public byte[] getByte(String url) {
        if (HttpManager.isBlockResource(url)) {
            VirgoLogger.httpDebug("getURL blockResource url={}", url);
            return new byte[]{};
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
        long start = System.currentTimeMillis();
        HttpMethod get = new GetMethod(url);
        int len = 0;
        try {
            len = doExecuteMethod(get, out);
            return out.toByteArray();
        } catch (ApiHttpClientException e) {
            return new byte[]{};
        } finally {
            accessLog(System.currentTimeMillis() - start, "GET",
                    get.getStatusLine() != null ? get.getStatusCode() : -1,
                    len, url, "", null);
        }
    }

    @Override
    public String post(String url, Map<String, ?> nameValues) {
        return post(url, nameValues, DEFAULT_CHARSET);
    }

    @Override
    public String post(String url, Map<String, ?> nameValues, String charset) {
        return post(url, nameValues, null, charset);
    }


    @Override
    public String post(String url, String jsonParam) {
        return post(url, jsonParam, null, DEFAULT_CHARSET);

    }

    private String post(String url, String jsonParam, Map<String, String> headers, String charset) {
        if (HttpManager.isBlockResource(url)) {
            VirgoLogger.httpDebug("requestURL blockResource url={}", url);
            return "";
        }
        PostMethod post = new PostMethod(url);
        try {
            post.setRequestEntity(new StringRequestEntity(jsonParam, "text/xml", "utf-8"));
            addHeader(post, headers);
            return executeMethod(url, post, jsonParam, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";

    }


    public String post(String url, Map<String, ?> nameValues, Map<String, String> headers, String charset) {
        if (HttpManager.isBlockResource(url)) {
            VirgoLogger.httpDebug("requestURL blockResource url={}", url);
            return "";
        }
        PostMethod post = new PostMethod(url);
        HttpMethodParams params = new HttpMethodParams();
        params.setContentCharset(charset);
        post.setParams(params);
        addHeader(post, headers);
        if (nameValues != null && !nameValues.isEmpty()) {
            List<NameValuePair> list = new ArrayList<NameValuePair>(
                    nameValues.size());
            for (Map.Entry<String, ?> entry : nameValues.entrySet()) {
                if (entry.getKey() != null && !entry.getKey().isEmpty()) {
                    list.add(new NameValuePair(entry.getKey(), entry
                            .getValue().toString()));
                } else {
                    try {
                        post.setRequestEntity(new StringRequestEntity(entry
                                .getValue().toString(), "text/xml", "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                    }
                }
            }
            if (!list.isEmpty()) {
                post.setRequestBody(list.toArray(new NameValuePair[list.size()]));
            }
        }
        return executeMethod(url, post, mapToString(nameValues), charset);
    }

    private static void addHeader(HttpMethod method, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                method.setRequestHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    public String postMulti(String url, Map<String, Object> nameValues) {
        return postMulti(url, nameValues, DEFAULT_CHARSET);
    }

    public String postMulti(String url, Map<String, Object> nameValues, String charset) {
        if (HttpManager.isBlockResource(url)) {
            VirgoLogger.httpDebug("multURLblockResource url={}", url);
            return "";
        }
        PostMethod post = new PostMethod(url);
        Part[] parts = new Part[nameValues.size()];
        if (nameValues != null && !nameValues.isEmpty()) {
            int i = 0;
            for (Map.Entry<String, Object> entry : nameValues.entrySet()) {
                if (entry.getValue() instanceof ByteArrayPart) {
                    ByteArrayPart data = (ByteArrayPart) entry.getValue();
                    parts[i++] = data;
                    continue;
                }
                if (entry.getValue() instanceof FileItem) {
                    FileItem item = (FileItem) entry.getValue();
                    String contentType = item.getContentType();
                    if ("application/octet-stream".equals(contentType)) {
                        contentType = "image/png";
                    }
                    parts[i++] = new ByteArrayPart(item.get(), entry.getKey(),
                            contentType);
                } else {
                    parts[i++] = new StringPart(entry.getKey(), entry
                            .getValue().toString(), "utf-8");
                }
            }
        }
        post.setRequestEntity(new MultipartRequestEntity(parts, post
                .getParams()));
        return executeMethod(url, post, mapToString(nameValues), charset);
    }

    public String postMulti(String url, InputStream in) {
        PostMethod post = new PostMethod(url);
        post.setRequestEntity(new InputStreamRequestEntity(in));
        return executeMethod(url, post, null, DEFAULT_CHARSET);
    }

    public String postMulti(String url, byte[] buf) {
        return this.postMulti(url, new ByteArrayInputStream(buf));
    }

    private byte[] executeMethodBytes(String url, HttpMethod method, ByteArrayOutputStream out, String charset) {
        long start = System.currentTimeMillis();
        int len = 0;
        try {
            len = doExecuteMethod(method, out);
            return out.toByteArray();
        } catch (ApiHttpClientException e) {
            return new byte[]{};
        } finally {
            accessLog(System.currentTimeMillis() - start, method.getName(),
                    method.getStatusLine() != null ? method.getStatusCode() : -1,
                    len, url, method.getQueryString(), null, "-");
        }
    }

    private String executeMethod(String url, HttpMethod method, String postString, String charset) {
        String result = null;
        long start = System.currentTimeMillis();
        int len = 0;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            len = doExecuteMethod(method, out);
            result = new String(out.toByteArray(), charset);
            return result;
        } catch (UnsupportedEncodingException e) {
            VirgoLogger.httpWarn(String.format("HttpServiceImpl.executeMethod UnsupportedEncodingException url:%s charset:%s", url, charset), e);
            return "";
        } catch (ApiHttpClientException e) {
            return "";
        } finally {
            accessLog(System.currentTimeMillis() - start, method.getName(),
                    method.getStatusLine() != null ? method.getStatusCode() : -1,
                    len, url, method.getQueryString(), postString, result);
        }
    }

    private int doExecuteMethod(HttpMethod httpMethod, OutputStream out) throws ApiHttpClientException {
        long start = System.currentTimeMillis();
        int readLen = 0;
        try {
            addRemoteInvokerHeader(httpMethod);
            client.executeMethod(httpMethod);
            if (System.currentTimeMillis() - start > this.soTimeOut) {
                throw new ReadTimeOutException(format(
                        "executeMethod so timeout time:%s soTimeOut:%s",
                        (System.currentTimeMillis() - start), soTimeOut));
            }
            InputStream in = httpMethod.getResponseBodyAsStream();
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = in.read(b)) > 0) {
                if (System.currentTimeMillis() - start > this.soTimeOut) {
                    throw new ReadTimeOutException(format(
                            "read so timeout time:%s soTimeOut:%s",
                            (System.currentTimeMillis() - start), soTimeOut));
                }
                out.write(b, 0, len);
                readLen += len;
                if (readLen > maxSize) {
                    throw new SizeException(
                            format("size too big size:%s maxSize:%s", readLen,
                                    maxSize)
                    );
                }
            }
            in.close();
        } catch (ApiHttpClientException ex) {
            VirgoLogger.httpWarn(
                    String.format("ApiHttpClientExcpetion url:%s message:%s", getHttpMethodURL(httpMethod), ex.getMessage()),
                    ex
            );
            throw ex;
        } catch (Exception ex) {
            VirgoLogger.httpWarn(
                    String.format("HttpServiceImpl.doExecuteMethod error! msg:%s", ex.getMessage()),
                    ex
            );
        } finally {
            httpMethod.releaseConnection();
        }
        return readLen;
    }

    private String getHttpMethodURL(HttpMethod httpMethod) {
        try {
            return httpMethod.getURI().toString();
        } catch (URIException e) {
            return "";
        }
    }

    public <T> T get(String url, ResultConvert<T> c) {
        String ret = get(url);
        return c.convert(url, null, ret);
    }

    public <T> T get(String url, String charset, ResultConvert<T> c) {
        String ret = get(url, charset);
        return c.convert(url, null, ret);
    }

    public <T> T post(String url, Map<String, ?> nameValues, ResultConvert<T> c) {
        String ret = post(url, nameValues);
        return c.convert(url, mapToString(nameValues), ret);
    }

    public <T> T post(String url, Map<String, ?> nameValues, String charset, ResultConvert<T> c) {
        String ret = post(url, nameValues, charset);
        return c.convert(url, mapToString(nameValues), ret);
    }

    public <T> T postMulti(String url, Map<String, Object> nameValues, String charset, ResultConvert<T> c) {
        String ret = postMulti(url, nameValues, charset);
        return c.convert(url, mapToString(nameValues), ret);
    }

    public RequestBuilder buildGet(String url) {
        HttpMethod get = new GetMethod(url);
        HttpClientRequestBuilder ret = new HttpClientRequestBuilder(url, get, HttpManager.isBlockResource(url));
        return ret;
    }

    public RequestBuilder buildPost(String url) {
        PostMethod post = new PostMethod(url);
        HttpClientRequestBuilder ret = new HttpClientRequestBuilder(url, post, HttpManager.isBlockResource(url));
        return ret;
    }

    public class HttpClientRequestBuilder implements RequestBuilder {
        private String url;
        private HttpMethod method;
        private boolean isBlock = false;
        private Map<String, String[]> queryParam = new HashMap<String, String[]>();
        private Map<String, String[]> bodyStringParam = new LinkedHashMap(16);
        private Map<String, Object> bodyBinParam = new LinkedHashMap(16);
        private String charset;

        public HttpClientRequestBuilder(String url, HttpMethod method, boolean isBlock) {
            this.url = url;
            this.method = method;
            this.isBlock = isBlock;
            HttpMethodParams params = new HttpMethodParams();
            params.setContentCharset(DEFAULT_CHARSET);
            params.setUriCharset(DEFAULT_CHARSET);
            method.setParams(params);
            charset = DEFAULT_CHARSET;
            if (isBlock) {
                VirgoLogger.httpDebug("blockResource url={}", url);
            }
        }

        @Override
        public RequestBuilder withCharset(String charset) {
            this.method.getParams().setContentCharset(charset);
            this.method.getParams().setUriCharset(charset);
            this.charset = charset;
            return this;
        }

        @Override
        public RequestBuilder withParam(Map<String, ?> param) {
            for (Map.Entry<String, ?> entry : param.entrySet()) {
                withParam(entry.getKey(), entry.getValue());
            }
            return this;
        }

        @Override
        public RequestBuilder withParam(boolean condition, String key, Object value) {
            if (condition) {
                return withParam(key, value);
            }
            return this;
        }

        @Override
        public RequestBuilder withParam(String key, Object value) {
            boolean isQueryParam = isQueryParam();
            Map<String, String[]> param = isQueryParam ? queryParam : bodyStringParam;
            if (value instanceof String) {
                addParameter(key, (String) value, param);
            } else if (value instanceof String[]) {
                addParameter(key, (String[]) value, param);
            } else if (value.getClass().isPrimitive()) {
                addParameter(key, value.toString(), param);
            } else if (value.getClass().isArray()) {
                int len = Array.getLength(value);
                for (int i = 0; i < len; i++) {
                    withParam(key, Array.get(value, i));
                }
            } else if (value instanceof Collection) {
                Iterator iter = ((Collection) value).iterator();
                while (iter.hasNext()) {
                    withParam(key, iter.next());
                }
            } else {
                if (isQueryParam) {
                    VirgoLogger.httpWarn(format(
                            "HttpClientRequestBuilder.withParam unsupport url:%s type:%s",
                            url, value.getClass().getSimpleName()));
                    addParameter(key, value.toString(), queryParam);
                } else {
                    bodyBinParam.put(key, value);
                }
            }
            return this;
        }

        public RequestBuilder withHeader(boolean condition, String key, String value) {
            if (condition) {
                return withHeader(key, value);
            }
            return this;
        }

        public RequestBuilder withHeader(String key, String value) {
            method.setRequestHeader(key, value);
            return this;
        }

        @Override
        public RequestBuilder withHeader(Map<String, String> header) {
            if (header != null) {
                for (Map.Entry<String, String> entry : header.entrySet())
                    method.addRequestHeader(entry.getKey(), entry.getValue());
            }
            return this;
        }

        @Override
        public <T> T execute(ResultConvert<T> convert) {
            return convert.convert(url, mapToString(bodyStringParam), execute());
        }

        @Override
        public String execute() {
            if (isBlock) {
                return "";
            }
            setQuery();
            setBody();
            String post = null;
            if (bodyStringParam.size() > 0) {
                post = mapToString(bodyStringParam);
            }
            return executeMethod(url, method, post, this.charset);
        }

        @Override
        public byte[] executeByte() {
            if (isBlock) {
                return new byte[]{};
            }
            setQuery();
            setBody();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            return executeMethodBytes(url, method, out, this.charset);
        }

        public Future<String> executeAsync() {
            Future<String> future = httpPool.submit(new Callable<String>() {
                public String call() throws Exception {
                    return execute();
                }
            });
            return future;
        }

        public String executeAsyncString() {
            Future<String> future = httpPool.submit(new Callable<String>() {
                public String call() throws Exception {
                    return execute();
                }
            });
            try {
                return future.get(HttpServiceImpl.this.soTimeOut, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                VirgoLogger.httpWarn(format("getAsync error url:%s post:%s msg:%s", url, mapToString(bodyStringParam), e.getMessage()));
                return "";
            }
        }

        private void setQuery() {
            if (queryParam.size() == 0) {
                return;
            }
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, String[]> entry : queryParam.entrySet()) {
                for (String item : entry.getValue()) {
                    sb.append(urlEncode(entry.getKey(), this.charset) + "=" + urlEncode(item, this.charset) + "&");
                }
            }
            TextUtil.trim(sb, '&');
            method.setQueryString(sb.toString());
        }

        private void setBody() {
            if (bodyBinParam.size() == 0 && bodyStringParam.size() == 0) {
                return;
            }
            boolean multi = bodyBinParam.size() > 0;
            if (multi) {
                setMultiBody();
            } else {
                setStringBody();
            }
        }

        private void setStringBody() {
            PostMethod postMethod = (PostMethod) method;
            List<NameValuePair> list = new ArrayList<NameValuePair>(
                    bodyStringParam.size());
            for (Map.Entry<String, String[]> entry : bodyStringParam.entrySet()) {
                String[] values = entry.getValue();
                for (String value : values) {
                    list.add(new NameValuePair(entry.getKey(), value));
                }
            }
            if (!list.isEmpty()) {
                postMethod.setRequestBody(list.toArray(new NameValuePair[list
                        .size()]));
            }
        }

        private void setMultiBody() {
            PostMethod postMethod = (PostMethod) method;
            List<Part> partList = new ArrayList<Part>();
            for (Map.Entry<String, String[]> entry : bodyStringParam.entrySet()) {
                for (String value : entry.getValue()) {
                    partList.add(new StringPart(entry.getKey(), value, "utf-8"));
                }
            }
            for (Map.Entry<String, Object> entry : bodyBinParam.entrySet()) {
                if (entry.getValue() instanceof ByteArrayPart) {
                    ByteArrayPart data = (ByteArrayPart) entry.getValue();
                    partList.add(data);
                } else if (entry.getValue() instanceof FileItem) {
                    FileItem item = (FileItem) entry.getValue();
                    String contentType = item.getContentType();
                    if ("application/octet-stream".equals(contentType)) {
                        contentType = "image/png";
                    }
                    partList.add(new ByteArrayPart(item.get(), entry.getKey(),
                            contentType));
                } else {
                    VirgoLogger.httpWarn(format(
                            "HttpClientRequestBuilder.setMultiBody unsupport url:%s type:%s",
                            url, entry.getValue().getClass().getSimpleName()));
                    partList.add(new StringPart(entry.getKey(), entry
                            .getValue().toString(), "utf-8"));
                }
            }
            postMethod.setRequestEntity(new MultipartRequestEntity(partList
                    .toArray(new Part[]{}), postMethod.getParams()));
        }

        public void addParameter(String name, String value, Map<String, String[]> paramMap) {
            addParameter(name, new String[]{value}, paramMap);
        }

        public void addParameter(String name, String[] values, Map<String, String[]> paramMap) {
            Assert.notNull(name, "Parameter name must not be null");
            String[] oldArr = (String[]) paramMap.get(name);
            if (oldArr != null) {
                String[] newArr = new String[oldArr.length + values.length];
                System.arraycopy(oldArr, 0, newArr, 0, oldArr.length);
                System.arraycopy(values, 0, newArr, oldArr.length, values.length);
                paramMap.put(name, newArr);
            } else {
                paramMap.put(name, values);
            }
        }

        private void addParameters(Map params) {
            Assert.notNull(params, "Parameter map must not be null");
            for (Iterator it = params.keySet().iterator(); it.hasNext(); ) {
                Object key = it.next();
                Assert.isInstanceOf(String.class, key,
                        "Parameter map key must be of type [" + String.class.getName() + "]");
                Object value = params.get(key);
                if (value instanceof String) {
                    this.addParameter((String) key, (String) value, queryParam);
                } else if (value instanceof String[]) {
                    this.addParameter((String) key, (String[]) value, queryParam);
                } else {
                    throw new IllegalArgumentException("Parameter map value must be single value " +
                            " or array of type [" + String.class.getName() + "]");
                }
            }
        }

        private boolean isQueryParam() {
            return method instanceof GetMethod || method instanceof DeleteMethod;
        }
    }

    private static String urlEncode(String str, String charset) {
        try {
            return urlCodec.encode(str, charset);
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    private void accessLog(long time, String method, int status, int len,
                           String uri, String queryString, String post) {
        accessLog(time, method, status, len, uri, queryString, post, "-");
    }

    private void accessLog(long time, String method, int status, int len,
                           String uri, String queryString, String post, String ret) {
        String url;
        if (StringUtils.isEmpty(queryString)) {
            url = uri;
        } else if (uri.contains("?")) {
            url = uri.substring(0, uri.indexOf("?") + 1) + queryString;
        } else {
            url = uri + "?" + queryString;
        }
        if (time > soTimeOut) {
            VirgoLogger.httpWarn("HTTP {} Error:{}", uri, time);
        }
        if (accessLog != null) {
            try {
                accessLog.accessLog(time, method, status, len, url, post, ret);
            } catch (Exception e) {
                VirgoLogger.httpWarn("error accessLog", e);
            }
        }
    }

    public class ApiHttpClientException extends Exception {
        public ApiHttpClientException(String msg) {
            super(msg);
        }
    }

    public class ReadTimeOutException extends ApiHttpClientException {
        public ReadTimeOutException(String msg) {
            super(msg);
        }
    }

    public class SizeException extends ApiHttpClientException {
        public SizeException(String msg) {
            super(msg);
        }

    }

    private void addRemoteInvokerHeader(HttpMethod httpMethod) {
        httpMethod.setRequestHeader("X-Remote-API-Invoker", "openapi");
    }

    private String mapToString(Map<String, ?> nameValues) {
        StringBuffer sb = new StringBuffer();
        if (nameValues == null) {
            return sb.toString();
        }
        for (Map.Entry<String, ?> entry : nameValues.entrySet()) {
            if (entry.getValue() instanceof String) {
                sb.append(entry.getKey() + "=" + entry.getValue() + "&");
            } else if (entry.getValue() instanceof String[]) {
                String[] values = (String[]) entry.getValue();
                for (String value : values) {
                    sb.append(entry.getKey() + "=" + value + "&");
                }
            }
        }
        TextUtil.trim(sb, '&');
        return sb.toString();
    }

}


class HttpManager {
    private static Set<String> blockResources = new HashSet<String>();

    public static void addBlockResource(String r) {
        if (r == null || r.length() < 6) {//http://
            return;
        }
        blockResources.add(r);
    }

    public static void removeBlockResource(String r) {
        blockResources.remove(r);
    }

    public static boolean isBlockResource(String url) {
        if (url == null) {
            return true;
        }
        for (String br : blockResources) {
            if (url.startsWith(br)) {
                return true;
            }
        }
        return false;
    }

    public static Set<String> getBlockResources() {
        return blockResources;
    }
}

class ByteArrayPart extends PartBase {
    private byte[] mData;

    private String mName;

    public ByteArrayPart(byte[] data, String name, String type) {
        super(name, type, "UTF-8", "binary");
        mName = name;
        mData = data;
    }

    protected void sendData(OutputStream out) throws IOException {
        out.write(mData);
    }

    protected long lengthOfData() throws IOException {
        return mData.length;
    }

    public byte[] getmData() {
        return mData;
    }

    protected void sendDispositionHeader(OutputStream out)
            throws IOException {
        super.sendDispositionHeader(out);
        StringBuilder buf = new StringBuilder();
        buf.append("; filename=\"").append(mName).append("\"");
        out.write(buf.toString().getBytes());
    }
}
