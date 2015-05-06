package com.winterfarmer.virgo.restapi.doc.generator;

import com.winterfarmer.virgo.account.model.GroupType;
import com.winterfarmer.virgo.account.model.RolePrivilege;
import com.winterfarmer.virgo.restapi.core.annotation.ResourceOverview;
import com.winterfarmer.virgo.restapi.core.annotation.RestApiInfo;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by yangtianhang on 15/4/26.
 */
public class InterfacePageGenerator extends DocGenerator {
    String indexTemplatePath = "doclet/Home.markdown";
    String indexDocPath = "Home.markdown";
    String packageBaseToScan = "com.winterfarmer.virgo";

    @Override
    public Map<String, String> prepare() throws Throwable {
        Map<String, String> rstmap = new HashMap<>();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream out = new PrintStream(baos, true, "UTF-8")) {
            // 1. get api version path
            Map<String, String> parentPathPackagePairs = RestapiDocTool.findParentUri(packageBaseToScan);
            for (Map.Entry<String, String> e : parentPathPackagePairs.entrySet()) {
                // write version
                out.println();
                out.println("#接口目录 v" + e.getKey().replace("/", ""));
                out.println();

                Map<Method, String> subResourceMethods = RestapiDocTool.findResourceMethods(e.getValue());
                Map<String, List<Method>> resourceMethods = RestapiDocTool.getResourceMethods(subResourceMethods);
                Map<ResourceOverview, String> resourceOverviewMap = RestapiDocTool.getResourceOverview(e.getValue());

                // write parent path, and all its subclass
                // (1)write parent path and classifier
                for (Map.Entry<ResourceOverview, String> entry : resourceOverviewMap.entrySet()) {
                    ResourceOverview overview = entry.getKey();
                    String parentPath = entry.getValue();

                    out.println();
                    out.println("## " + parentPath + NEWLINE + overview.value());
                    out.println();

                    //(2) write sub apis
                    List<Method> mList = resourceMethods.get(parentPath);

                    //(3) print table
                    out.println("> 接口地址 | 接口描述|认证策略|权限");
                    out.println(">--------|--------|--------|--------");

                    for (Method m : mList) {
                        String path = e.getKey() + subResourceMethods.get(m);
                        String doclink = path.substring(1).replace('/', '_').replace(".json", "");

                        RestApiInfo apiInfo = m.getAnnotation(RestApiInfo.class);

                        String desc = apiInfo.desc();
                        // 没有 api status这个
//                        String imageUrl = apiInfo.apiStatus() == RestApiInfo.ApiStatus.PUBLIC ? "https://assets-cdn.github.com/images/icons/emoji/octocat.png" : "https://assets-cdn.github.com/images/icons/emoji/octopus.png";
//                        boolean isDeprecated = apiInfo.apiStatus() == RestApiInfo.ApiStatus.DEPRECATED;
//                        String apistatus = "<img src='" + imageUrl + "'  width='20px'  height='20px'/>";

                        String authPolicy = apiInfo.authPolicy().desc();
                        String adminRolesString = "无";

                        if (apiInfo.authPolicy() == RestApiInfo.AuthPolicy.INTERNAL) {
                            GroupType adminCate = apiInfo.groupType();
                            adminRolesString = adminCate.name();

                            if (adminCate == GroupType.PUBLIC) {
                            } else {
                                RolePrivilege[] methods = apiInfo.rolePrivileges();
                                for (RolePrivilege method : methods) {
                                    adminRolesString = adminRolesString + method.getName();
                                }
                            }
                        }

                        String interfaceUrl = "[" + path.split("/", 4)[3] + "](" + doclink + ")  ";

                        out.println(
                                "> " + interfaceUrl + " | " + desc + " | " + authPolicy + " | " + adminRolesString + " "
                        );

                        // (4) generate concrete wiki for api.
                        WikiPageGenerator wpg = new WikiPageGenerator(e.getKey(), doclink, path, m, apiInfo);
                        wpg.run();
                    }
                }
            }
            rstmap.put("{indexTables}", baos.toString("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(InterfacePageGenerator.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            return rstmap;
        }
    }

    @Override
    public String getTemplatePath() {
        return templateBase + SEP + "ErrorDesc.markdown";
    }

    @Override
    public String getDocPath() {
        return defaultDocBase + SEP + "base" + SEP + "error_code_info.md";
    }

    @Override
    public void postActions() {

    }
}
