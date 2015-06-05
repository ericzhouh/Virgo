package com.winterfarmer.virgo.restapi.doc.generator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.winterfarmer.virgo.base.annotation.ApiMode;
import com.winterfarmer.virgo.base.model.CommonResult;
import com.winterfarmer.virgo.restapi.core.annotation.ParamSpec;
import com.winterfarmer.virgo.restapi.core.annotation.RestApiInfo;
import com.winterfarmer.virgo.restapi.core.exception.RestExceptionFactor;
import com.winterfarmer.virgo.restapi.core.param.AbstractParamSpec;
import com.winterfarmer.virgo.restapi.core.param.ParamSpecFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by yangtianhang on 15/4/26.
 */
public class WikiPageGenerator extends DocGenerator {

    private String docParentDir;

    private String docFilePath;

    private String resourcePath;

    private Method resourceMethod;

    private RestApiInfo restApiInfo;

    private final String sectionLine = "-----------------------------";

    public WikiPageGenerator(String docParentDir, String docFilePath, String resourcePath, Method resourceMethod, RestApiInfo restApiInfo) {
        this.docParentDir = docParentDir;
        this.docFilePath = docFilePath + ".md";
        this.resourcePath = resourcePath;
        this.resourceMethod = resourceMethod;
        this.restApiInfo = restApiInfo;
    }

    @Override
    public Map<String, String> prepare() throws Throwable {
        Map<String, String> rstMap = new HashMap<>();
        rstMap.put("{requestURI}", resourcePath);
        rstMap.put("{apidesc}", restApiInfo.desc());
        rstMap.put("{protocol}", restApiInfo.protocol().name().toLowerCase());
        rstMap.put("{httpMethod}", resolveMethods(resourceMethod));
        rstMap.put("{consumeMediaType}", resolveMIMEType(resourceMethod, "Consumes"));
        rstMap.put("{produceMediaType}", resolveMIMEType(resourceMethod, "Produces"));
        rstMap.put("{authPolicy}", restApiInfo.authPolicy().name());
        String cautions = resolveStringArray("> ", restApiInfo.cautions());
        rstMap.put("{cautions}", StringUtils.isBlank(cautions) ? "无" : cautions);
        rstMap.put("{errors}", resolveErrors(restApiInfo.errors()));
        Class returnType = this.resourceMethod.getReturnType();
        boolean isCollection = false;
        if (returnType.isArray()
                || returnType.isAssignableFrom(Collection.class)
                || returnType.isAssignableFrom(List.class)
                || returnType.isAssignableFrom(Set.class)) {
            isCollection = true;
        }

        rstMap.put("{retRst}", (isCollection ? "集合: " : "单独实体: ") + resolveDemoWikiLink(restApiInfo.resultDemo()));
        rstMap.put("{retRstJSON}", resolveDemoJSON(isCollection, restApiInfo.resultDemo()));
        rstMap.putAll(resolveParamTable());

        return rstMap;
    }

    private String resolveErrors(RestExceptionFactor[] errors) {
        StringBuilder sb = new StringBuilder();
        sb.append(RestExceptionFactor.INVALID_PARAM.getHttpErrorCode()).
                append("|").append(RestExceptionFactor.INVALID_PARAM.getInnerErrorCode())
                .append("|").append(RestExceptionFactor.INVALID_PARAM.getReason()).append(NEWLINE);

        sb.append(RestExceptionFactor.MISSING_PARAM.getHttpErrorCode()).
                append("|").append(RestExceptionFactor.MISSING_PARAM.getInnerErrorCode())
                .append("|").append(RestExceptionFactor.MISSING_PARAM.getReason()).append(NEWLINE);

        sb.append(RestExceptionFactor.INTERNAL_SERVER_ERROR.getHttpErrorCode()).
                append("|").append(RestExceptionFactor.INTERNAL_SERVER_ERROR.getInnerErrorCode())
                .append("|").append(RestExceptionFactor.INTERNAL_SERVER_ERROR.getReason()).append(NEWLINE);

        for (RestExceptionFactor ref : errors) {
            sb.append(ref.getHttpErrorCode()).append("|").append(ref.getInnerErrorCode()).append("|").append(ref.getReason()).append(NEWLINE);
        }

        return sb.toString();
    }

    private String resolveDemoJSON(boolean isCollection, Class returnType) {
        try {
            String demoJson;
            Object object = this.createNewInstance(returnType);
            if (isCollection) {
                demoJson = JSON.toJSONString(object, features);
            } else {
                List<Object> list = Lists.newArrayList();
                list.add(object);
                list.add(this.createNewInstance(returnType));
                demoJson = JSON.toJSONString(list, features);
            }

            demoJson = RestapiDocTool.prettyPrintJson(demoJson).trim() + NEWLINE;
            return demoJson;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private String resolveDemoWikiLink(Class aClass) {
        String link = "[#mode_name#](#mode_link#) ";
        ApiMode apiMode = (ApiMode) aClass.getAnnotation(ApiMode.class);
        if (aClass == CommonResult.class) {
            return "通用对象 (CommonResult)";
        }

        if (apiMode == null) {
            return "未知对象";
        }

        StringBuffer sb = new StringBuffer("");
        sb.append(link.replaceAll("#mode_name#", apiMode.desc()).replaceAll("#mode_link#", "base_api_json_model#" + aClass.getCanonicalName())).append(" \n");
        return sb.toString();
    }

    private Map<String, String> resolveParamTable() {
        Map<String, String> rstMap = new HashMap<>();

        StringBuilder postParam = new StringBuilder(" ");
        StringBuilder getParam = new StringBuilder(" ");

        boolean isMultiPart = isMultiPart();
        String p = isMultiPart ? "-F" : "-d";
        resolveCurlMethod(postParam);
        Annotation[][] pms_anno = resourceMethod.getParameterAnnotations();
        Class<?>[] paramClazzes = resourceMethod.getParameterTypes();
        String apiargs = "";
        int i = 0;
        int optparam = 0;
        // writing parameters
        for (Annotation[] annotations : pms_anno) {
            boolean isParam = false;
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(ParamSpec.class)) {
                    isParam = true;
                }
            }
            if (!isParam) {
                i++;
                continue;
            }
            Class<?> param = paramClazzes[i++];
            String name = "";
            boolean required = false;
            String type = parseTypeStr(param);
            String desc = "";
            boolean isPathParam = false;
            String spec = "";
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(ParamSpec.class)) {
                    ParamSpec paramDesc = (ParamSpec) annotation;
                    required = paramDesc.isRequired();
                    desc = paramDesc.desc();
                    spec = paramDesc.spec();
                    if (StringUtils.isNotBlank(type)) {
                        type = type + ": " + paramDesc.spec();
                    } else {
                        type = paramDesc.spec();
                    }
                }
                if (annotation.annotationType().getSimpleName()
                        .endsWith("Param")) {
                    name = annotation.toString();
                    int s = name.indexOf("(value=") + 7;
                    int e = name.length() - 1;
                    name = name.substring(s, e);
                }
                if (annotation.annotationType().equals(DefaultValue.class)) {
                    desc = desc + "默认为" + ((DefaultValue) annotation).value();
                }
                if (annotation.annotationType().equals(PathParam.class)) {
                    isPathParam = true;
                }
            }
            if (param.isEnum()) {
                desc = desc + resolveEnumType(param);
            }

            if (!(name.equals("source") || name.equals("format"))) {
                apiargs = apiargs + "> " + name + "|" + required + "|"
                        + type + "|" + desc;
                if (i < paramClazzes.length) {
                    apiargs = apiargs + NEWLINE;
                }

                if (required) {
                    generateCurlParamDemo(isMultiPart, isPathParam, name, type,
                            spec, p, postParam, getParam);
                } else if (!required && optparam <= 0) {
                    generateCurlParamDemo(isMultiPart, isPathParam, name, type,
                            spec, p, postParam, getParam);
                    optparam++;
                }
            }

        }

        // writing parameter extra desc
        apiargs = apiargs + NEWLINE + NEWLINE + resolveStringArray("+ ", restApiInfo.extraParamDesc()) + NEWLINE;

        rstMap.put("{paramTable}", apiargs);

        //writing curl demo
        try {
            if (resourceMethod.isAnnotationPresent(GET.class)) {
                postParam.delete(0, postParam.length());
                if (getParam.length() >= 2 && getParam.substring(0, 2).trim().equals("&")) {
                    getParam.replace(0, 2, "?");
                }
            } else if (resourceMethod.isAnnotationPresent(POST.class)) {
                getParam.delete(0, getParam.length());
                if (isMultiPart) {
                    postParam.deleteCharAt(0);
                } else {
                    if (postParam.length() >= 2 && postParam.substring(postParam.length() - 1, postParam.length()).trim().equals("&")) {
                        postParam.deleteCharAt(postParam.length() - 1).deleteCharAt(0)
                                .insert(0, "-d \"").append("\"");
                    }

                }
            }
        } catch (Exception e) {
            Logger.getLogger(WikiPageGenerator.class.getName()).log(Level.SEVERE,
                    null, e);
            throw e;
        } finally {
            rstMap.put("{curlPostParam}", postParam.toString());
            rstMap.put("{curlGetParam}", getParam.toString());
        }

        return rstMap;
    }

    private String resolveEnumType(Class<?> param) {
        return Joiner.on(",").join(param.getEnumConstants());
    }

    private boolean isMultiPart() {
        if (resourceMethod.isAnnotationPresent(Consumes.class)) {
            Consumes c = resourceMethod.getAnnotation(Consumes.class);
            List<String> mineType = Arrays.asList(c.value());
            if (mineType.contains(MediaType.MULTIPART_FORM_DATA)) {
                return true;
            }
        }
        return false;
    }

    private void resolveCurlMethod(StringBuilder postParam) {
        if (resourceMethod.isAnnotationPresent(PUT.class)) {
            postParam.append("-X PUT");
        } else if (resourceMethod.isAnnotationPresent(DELETE.class)) {
            postParam.append("-X DELETE");
        }
    }

    private String parseTypeStr(Class<?> param) {
        String type = param.getSimpleName().toLowerCase() + "";

//        if (type.equals("long")) {
//            type = "int64";
//        }
//        if (type.equals("double")) {
//            type = "float64";
//        }
        if (type.contains("stream") || type.contains("data")) {
            return "binary";
        } else {
            return "";
        }
    }


    public static final SerializerFeature[] features = new SerializerFeature[]{
            SerializerFeature.WriteNullBooleanAsFalse, SerializerFeature.WriteNullStringAsEmpty
            , SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullNumberAsZero
            , SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.PrettyFormat
    };


    private String resolveDemoJson(Class clz) {
        String demoJson = "\t";
        try {
            demoJson = JSON.toJSONString(this.createNewInstance(clz), features);
            demoJson = RestapiDocTool.prettyPrintJson(demoJson).trim() + NEWLINE;
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(WikiPageGenerator.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            return demoJson;
        }
    }

    private String resolveStringArray(String linePrefix, String[] cautions) {
        StringBuilder sb = new StringBuilder(128);
        if (ArrayUtils.isEmpty(cautions)) {
            return "";
        }
        for (String c : cautions) {
            c = c.replace("\n", "\n" + linePrefix).trim();
            if (c.endsWith(linePrefix.trim())) {
                c = c.substring(0, c.length() - linePrefix.length());
            }
            sb.append(linePrefix).append(c)
                    .append(NEWLINE).append(NEWLINE).append(sectionLine).append(NEWLINE);
        }
        String rst = StringUtils.trim(sb.toString());
        if (rst.endsWith(linePrefix.trim())) {
            rst = rst.substring(0, rst.length() - linePrefix.length());
        }
        return rst;
    }

    private String resolveMethods(Method m) {
        Annotation[] annos = m.getAnnotations();
        HttpMethod httpMet = null;
        for (Annotation a : annos) {
            if (a.annotationType().isAnnotationPresent(HttpMethod.class)) {
                httpMet = a.annotationType().getAnnotation(HttpMethod.class);
            }
        }

        if (httpMet != null) {
            return httpMet.value().toUpperCase();
        }
        return "#UNSURE#";
    }

    /**
     * Get response format according to Produces and Consumes annotation
     *
     * @param resourceMethod
     * @param type
     * @return
     */
    private String resolveMIMEType(Method resourceMethod, String type) {
        String rst = MediaType.WILDCARD;

        if ("Consumes".equalsIgnoreCase(type)) {//判断Consumes的MIME type
            if (resourceMethod.isAnnotationPresent(Consumes.class)) {// 有Consumes注解的情况
                if (!ArrayUtils.contains(resourceMethod.getAnnotation(Consumes.class).value(), MediaType.MULTIPART_FORM_DATA)) {//不是MULTIPART的情况
                    if (resourceMethod.isAnnotationPresent(POST.class)) {//如果不是MULTIPART但是是POST请求，则为FORM_URLENCODED
                        rst = MediaType.APPLICATION_FORM_URLENCODED;
                        return rst;
                    }
                } else {
                    rst = Joiner.on(",").skipNulls().join(resourceMethod.getAnnotation(Consumes.class).value());
                    return rst;
                }
            }
        }

        if ("Produces".equalsIgnoreCase(type)) {
            rst = Joiner.on(",").skipNulls().join(resourceMethod.getAnnotation(Produces.class).value());
            return rst;
        }

        return rst;
    }

    @Override
    public String getTemplatePath() {
        return TEMPLATE_BASE + SEP + "ApiPage.markdown";
    }

    @Override
    public String getDocPath() {
        return defaultDocBase + SEP
                + "apidocs" + docParentDir
                + SEP + docFilePath;
    }

    @Override
    public void postActions() {
        return;
    }

    private void generateCurlParamDemo(boolean isMultiPart,
                                       boolean isPathParam, String name, String type, String strSpec,
                                       String p, StringBuilder postParam, StringBuilder getParam) {
        String value = resolveDemoParamValue(type, strSpec);
        if (resourceMethod.isAnnotationPresent(POST.class)) {
            if (isMultiPart) {
                postParam.append(" ").append(p).append(" \"").append(name)
                        .append("=");
                if (type.equals("binary")) {
                    postParam.append("@");
                }
                postParam.append(value).append("\"");
            } else {
                postParam.append(name).append("=").append(value).append("&");
            }
        }
        if (!isPathParam) {
            getParam.append("&").append(name).append("=").append(value);
        }
    }

    private String resolveDemoParamValue(String type, String strSpec) {
        return type.equals("binary") ? "1.jpg" : resolveNoBinaryDemoParamValue(type, strSpec);
    }

    private String resolveNoBinaryDemoParamValue(String type, String strSpec) {
        if (StringUtils.isNotEmpty(strSpec)) {
            try {
                AbstractParamSpec spec = ParamSpecFactory.getParamSpec(strSpec);
                return spec.sample();
            } catch (Exception e) {
                return resolveValueByType(type);
            }
        } else {
            return resolveValueByType(type);
        }
    }

    private String resolveValueByType(String type) {
        String result = "{value}";
//        if (type.equals("int")) {
//            result = new IntRange(1, 100).getBaseSample();
//        } else if (type.equals("int64")) {
//            result = new LongRange(1000000l, 10000000l).getBaseSample();
//        } else if (type.equals("float")) {
//            result = new FloatRange(1f, 100f).getBaseSample();
//        } else if (type.equals("float64")) {
//            result = new DoubleRange(1d, 1000000d).getBaseSample();
//        } else if (type.equals("string")) {
//            result = "ABC";
//        }

        return result;
    }

    private Object createNewInstance(Class clz) throws Exception {
        if (clz == null) {
            return null;
        }

        try {
            Constructor zeroArgConstructor = null;
            Constructor argConstructor = null;
            int lastArgNums = 10000;
            for (Constructor c : clz.getDeclaredConstructors()) {
                if (c.getGenericParameterTypes().length == 0) {
                    zeroArgConstructor = c;
                    break;
                } else {
                    if (c.getGenericParameterTypes().length < lastArgNums) {
                        argConstructor = c;
                        lastArgNums = c.getGenericParameterTypes().length;
                    }
                }
            }
            if (zeroArgConstructor != null) {
                return zeroArgConstructor.newInstance();
            } else {

                Object[] arr = new Object[argConstructor.getGenericParameterTypes().length];
                for (int i = 0; i < argConstructor.getGenericParameterTypes().length; i++) {
                    Type t = argConstructor.getGenericParameterTypes()[i];
                    arr[i] = Class.forName(((Class) t).getCanonicalName()).newInstance();
                }

                return argConstructor.newInstance(arr);
            }

        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(WikiPageGenerator.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }


    public static void main(String[] args) {

//        try {
//            Class clz = ApiUser.class;
//            Constructor zeroArgConstructor = null;
//            Constructor argConstructor = null;
//            int lastArgNums = 10000;
//            for (Constructor c : clz.getDeclaredConstructors()) {
//                if (c.getGenericParameterTypes().length == 0) {
//                    zeroArgConstructor = c;
//                    break;
//                } else {
//                    if (c.getGenericParameterTypes().length < lastArgNums) {
//                        argConstructor = c;
//                        lastArgNums = c.getGenericParameterTypes().length;
//                    }
//                }
//            }
//            if (zeroArgConstructor != null) {
//                System.out.println(zeroArgConstructor.newInstance());
//            } else {
//
//                // TODO: fix args generating: currently, we don't support any model class without a default constructor
//
//                Object[] arr = new Object[argConstructor.getGenericParameterTypes().length];
//                for (int i = 0; i < argConstructor.getGenericParameterTypes().length; i++) {
//                    Type t = argConstructor.getGenericParameterTypes()[i];
//                    arr[i] = Class.forName(((Class) t).getCanonicalName()).newInstance();
//                    // TODO: fix args generating
//                }
//
//                System.out.println(argConstructor.newInstance(arr));
////                return new CommonResult();
//            }
//
//        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
//            System.out.println(ex);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
    }

}
