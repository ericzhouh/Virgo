package com.winterfarmer.virgo.restapi.core.filter;

import com.alibaba.fastjson.JSON;
import com.winterfarmer.virgo.log.LogHelper;
import com.winterfarmer.virgo.log.VirgoLogger;
import com.winterfarmer.virgo.restapi.core.request.ContainerRequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.internal.util.collection.StringIgnoreCaseKeyComparator;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.*;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.util.*;

/**
 * Created by yangtianhang on 15-1-7.
 */
@PreMatching
@Priority(FilterPriorities.LOGGER)
public class VirgoLogFilter implements ContainerRequestFilter, ContainerResponseFilter, WriterInterceptor {
    private static final Comparator<Map.Entry<String, List<String>>> COMPARATOR =
            new Comparator<Map.Entry<String, List<String>>>() {
                @Override
                public int compare(final Map.Entry<String, List<String>> o1, final Map.Entry<String, List<String>> o2) {
                    return StringIgnoreCaseKeyComparator.SINGLETON.compare(o1.getKey(), o2.getKey());
                }
            };


    private Set<Map.Entry<String, List<String>>> getSortedHeaders(final Set<Map.Entry<String, List<String>>> headers) {
        final TreeSet<Map.Entry<String, List<String>>> sortedHeaders = new TreeSet<Map.Entry<String, List<String>>>(COMPARATOR);
        sortedHeaders.addAll(headers);
        return sortedHeaders;
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        LogHelper.startRestLog();
        String httpMethod = requestContext.getMethod();
        String url = requestContext.getUriInfo().getRequestUri().getPath();

        LogHelper.setUrl(url);
        boolean isMultipart = ContainerRequestUtil.isMultipartRequest(requestContext);
        String accessToken = ContainerRequestUtil.getAccessToken(requestContext);

        if (!ignoreLog(url)) {
            VirgoLogger.logRequest("start:{}, {}, {}, {}, {}",
                    isMultipart ? "Multipart" : httpMethod,
                    url,
                    StringUtils.isBlank(accessToken) ? "null" : accessToken,
                    getHeaderString(requestContext),
                    JSON.toJSONString(ContainerRequestUtil.getParametersMap(requestContext)));
        }
    }

    private String getHeaderString(ContainerRequestContext requestContext) {
        Set<Map.Entry<String, List<String>>> set = getSortedHeaders(requestContext.getHeaders().entrySet());
        StringBuilder rst = new StringBuilder("[ ");
        for (final Map.Entry<String, List<String>> headerEntry : set) {
            final List<?> val = headerEntry.getValue();
            final String header = headerEntry.getKey();

            if ("x-scheme".equalsIgnoreCase(header)
                    || "connection".equalsIgnoreCase(header)
                    || "accept-language".equalsIgnoreCase(header)
                    || "content-length".equalsIgnoreCase(header)
                    || "content-type".equalsIgnoreCase(header)
                    || "charset".equalsIgnoreCase(header)
                    || "host".equalsIgnoreCase(header)
                    || "cookie".equalsIgnoreCase(header)
                    ) { //过滤这个header
                continue;
            }
            if (val.size() == 1) {
                rst.append(header).append(": ").append(val.get(0)).append(",");
            } else {
                final StringBuilder sb = new StringBuilder();
                boolean add = false;
                for (final Object s : val) {
                    if (add) {
                        sb.append(',');
                    }
                    add = true;
                    sb.append(s);
                }
                rst.append(header).append(": ").append(sb.toString()).append(",");
            }

        }
        rst.deleteCharAt(rst.length() - 1);
        rst.append("  ]");
        return rst.toString();
    }

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
        LogHelper.setHttpStatus(responseContext.getStatus());
    }

    @Override
    public void aroundWriteTo(final WriterInterceptorContext writerInterceptorContext) throws IOException, WebApplicationException {
        writerInterceptorContext.proceed();
        long endTime = System.currentTimeMillis();
        String url = LogHelper.getUrl();

        if (!ignoreLog(url)) {
            VirgoLogger.logResponse("end:{}, {}, [{}]",
                    endTime - LogHelper.getStartTime(), LogHelper.getHttpStatus(),
                    JSON.toJSONString(writerInterceptorContext.getEntity()));
        }

        LogHelper.endRestLog();
    }

    private boolean ignoreLog(String url) {
        return StringUtils.isNotBlank(url) && url.endsWith("upstream_check");
    }
}
