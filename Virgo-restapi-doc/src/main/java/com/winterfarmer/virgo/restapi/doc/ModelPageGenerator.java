package com.winterfarmer.virgo.restapi.doc;

import com.alibaba.fastjson.annotation.JSONField;
import com.winterfarmer.virgo.base.annotation.ApiField;
import com.winterfarmer.virgo.base.annotation.ApiMode;
import com.winterfarmer.virgo.restapi.core.provider.EnumParamConverterProvider;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.*;
import java.security.AccessController;
import java.util.*;

/**
 * Created by yangtianhang on 15/4/26.
 */
public class ModelPageGenerator extends DocGenerator {
//    private final String indexTemplatePath = "doclet/Home.markdown";
//    private final String indexDocPath = "Home.markdown";
    
    private final String packageBaseToScan = "com.ricebook";

    @Override
    public Map<String, String> prepare() throws Throwable {
        Map<String, String> rstmap = new HashMap<>();

/**
 * <a name="md-json-" id="md-json-"></a>
 Oauth 2.0相关接口


 >--------|--------|--------|--------|--------
 >city_id |获取用户的access_token | 公开的接口 |[文内链接](#md-json-classname)|无

 {models}
 */

//find all class with ApiMode

        Set<Class> apiModels = RestapiDocTool.findJSONModelClass(packageBaseToScan);

        StringBuilder sb = new StringBuilder("");
        StringBuilder titleSb = new StringBuilder("");

        for (Class clazz : apiModels) {
            ApiMode apiMode = (ApiMode) clazz.getAnnotation(ApiMode.class);
            String link = " [#mode_name#](#mode_link#);";
            titleSb.append("*").
                    append(link.replaceAll("#mode_name#", apiMode.desc()).replaceAll("#mode_link#", "#" + clazz.getCanonicalName())).
                    append(" \n");

            String title = "\n" +
                    "<a name=\"#mode_link#\" id=\"#mode_link#\"></a>\n" +
                    "#mode_desc#\n" +
                    "\n";
            title = title.replaceAll("#mode_desc#", apiMode.desc()).replaceAll("#mode_link#", clazz.getCanonicalName());
            sb.append(title);
            sb.append(">JSON字段名 | 字段描述|是否集合|数据类型|枚举含义 \n");
            sb.append(">--------|--------|--------|--------|-------- \n");

            appendFieldInfo(sb, clazz);
        }

        titleSb.append(" \n").append(sb.toString());
        rstmap.put("{json_models}", titleSb.toString());
        return rstmap;
    }

    private void appendFieldInfo(StringBuilder sb, Class clazz) {
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(ApiField.class)) {
                ApiField apiField = field.getAnnotation(ApiField.class);
                JSONField jsonField = field.getAnnotation(JSONField.class);
                String jsonFileName = jsonField.name();
                String desc = apiField.desc();
                boolean isCollection = false;

                String fieldTypeText;
                if (ArrayUtils.isNotEmpty(apiField.referClass())) {
                    StringBuffer s1 = new StringBuffer();
                    for (Class c : apiField.referClass()) {
                        s1.append(getClassTypeString(c)).append("  ");
                    }
                    fieldTypeText = s1.toString();
                } else {
                    Class fieldType = field.getType();
                    if (fieldType.isArray()
                            || fieldType.isAssignableFrom(Collection.class)
                            || fieldType.isAssignableFrom(List.class)
                            || fieldType.isAssignableFrom(Set.class)) {
                        isCollection = true;
                    }
                    fieldTypeText = getFieldTypeText(field, isCollection, fieldType);

                }

                String enumText = getEnumText(apiField);
                String isCollectionString = isCollection ? "是" : "否";
                sb.append("> " + jsonFileName + " | " + desc + " | " + isCollectionString + " | " + fieldTypeText + " | " + enumText + "  \n");
            }
        }
    }

    private String getEnumText(ApiField apiField) {
        if (apiField.targetEnum().isEnum()) {
            Object[] enumObjects = apiField.targetEnum().getEnumConstants();
//            Method fromStringMethod = AccessController.doPrivileged(ReflectionHelper.getFromStringStringMethodPA(apiField.targetEnum()));
            Method valueByIndexMethod = AccessController.doPrivileged(EnumParamConverterProvider.getValueByIndexStringMethodPA(apiField.targetEnum()));
            StringBuilder sb = new StringBuilder();
            if (valueByIndexMethod != null) { //我们系统内常用的枚举类型
                for (Object enumObj : enumObjects) {
                    try {
                        Method getName = enumObj.getClass().getDeclaredMethod("getName", null);
                        Method getIndex = enumObj.getClass().getDeclaredMethod("getIndex", null);

                        String name = (String) getName.invoke(enumObj, null);
                        Integer index = (Integer) getIndex.invoke(enumObj, null);

                        sb.append(name).append("=").append(index).append(" ");
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                return sb.toString();
            } else {
                return "默认枚举,暂不支持";
            }
        }

        return "";
    }

    private String getFieldTypeText(Field field, boolean isCollection, Class fieldType) {

        String fieldTypeText;
        Class fieldTypeClass = null;

        if (isCollection) {
            if (fieldType.isAssignableFrom(List.class)) {
                Type fc = field.getGenericType();
                if (fc instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) fc;

                    fieldTypeClass = (Class) pt.getActualTypeArguments()[0];
                }
            } else {
                fieldTypeClass = fieldType.getComponentType();
            }

        } else {
            fieldTypeClass = field.getType();
        }

        fieldTypeText = getClassTypeString(fieldTypeClass);
        return fieldTypeText;
    }

    private String getClassTypeString(Class fieldTypeClass) {
        String fieldTypeText;
        if (fieldTypeClass.isAnnotationPresent(ApiMode.class)) {
            fieldTypeText = "[#mode_name#](#mode_link#)";
            ApiMode fieldApiModel = (ApiMode) fieldTypeClass.getAnnotation(ApiMode.class);
            fieldTypeText = fieldTypeText.replaceAll("#mode_name#", fieldApiModel.desc())
                    .replaceAll("#mode_link#", "#" + fieldTypeClass.getCanonicalName());
        } else {
            if (fieldTypeClass == Object.class) {
                fieldTypeText = "通用对象";
            } else {
                fieldTypeText = fieldTypeClass.getSimpleName();
            }

        }
        return fieldTypeText;
    }


    @Override
    public String getTemplatePath() {
        return templateBase + SEP + "api_mode.markdown";
    }

    @Override
    public String getDocPath() {
        return defaultDocBase + SEP + "base" + SEP + "base_api_json_model.md";
    }

    @Override
    public void postActions() {
    }
}
