package com.winterfarmer.virgo.restapi.doc;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.winterfarmer.virgo.restapi.core.annotation.ResourceOverview;
import org.apache.commons.lang3.ArrayUtils;
import org.glassfish.jersey.server.ResourceFinder;
import org.glassfish.jersey.server.internal.scanning.AnnotationAcceptingListener;
import org.glassfish.jersey.server.internal.scanning.PackageNamesScanner;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by yangtianhang on 15/4/26.
 */
public class RestapiDocTool {
    private static final String PACKAGE_ROOT = "com.winterfarmer.virgo";
    public static final String JSON_FORMATTER = "doclet/dumpjson.js";

    public static void main(String[] args) {
        Map<String, String> parentUriMap = findParentUri(PACKAGE_ROOT);
        for (Map.Entry<String, String> e : parentUriMap.entrySet()) {
            System.out.println(e.getKey() + " " + e.getValue());
        }

        Map<Method, String> resourceMethods = findResourceMethods(
                parentUriMap.values().toArray(new String[parentUriMap.size()]));

        for (Map.Entry<Method, String> e : resourceMethods.entrySet()) {
            System.out.println(e.getKey().getName() + " " + e.getValue());
        }
    }

    public static Map<String, String> findParentUri(String... packageNames) {
        PackageNamesScanner scanner = new PackageNamesScanner(packageNames, true);
        AnnotationAcceptingListener listener = new AnnotationAcceptingListener(ApplicationPath.class);

        Map<String, String> map = new HashMap<>();
        scanClass(scanner, listener);

        Set<Class<?>> subTypes = listener.getAnnotatedClasses();
        for (Class<?> clazz : subTypes) {
            if (clazz.isAnnotationPresent(ApplicationPath.class)) {
                try {
                    map.put(resolvePathToFormal(clazz.getAnnotation(ApplicationPath.class).value()),
                            (String) clazz.getDeclaredField("apiResourceClassPackage").get(clazz));
                } catch (NoSuchFieldException ex) {
                    Logger.getLogger(RestapiDocTool.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger(RestapiDocTool.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(RestapiDocTool.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(RestapiDocTool.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return map;
    }

    private static void scanClass(ResourceFinder scanner, AnnotationAcceptingListener listener) {
        while (scanner.hasNext()) {
            String resource = scanner.next();
            if (listener.accept(resource)) {
                try (final InputStream in = scanner.open()) { // java7新特性 auto closable
                    listener.process(resource, in);
                } catch (IOException e) {
                    //TODO: using logger
                    e.printStackTrace();
                }
            }
        }
    }

    public static String resolvePathToFormal(String path) {
        String rst = path;
        if (!rst.startsWith("/")) {
            rst = "/" + rst;
        }

        if (rst.endsWith("/")) {
            rst = rst.substring(0, rst.length() - 1);
        }

        return rst;
    }

    public static Map<Method, String> findResourceMethods(String... packageNames) {
        if (ArrayUtils.isEmpty(packageNames)) {
            return Collections.emptyMap();
        }
        Map<Method, String> map = new ConcurrentHashMap<>();
        PackageNamesScanner scanner = new PackageNamesScanner(packageNames, true);
        AnnotationAcceptingListener afl = new AnnotationAcceptingListener(Path.class);

        scanClass(scanner, afl);

        Set<Class<?>> subTypes = afl.getAnnotatedClasses();
        for (Class<?> clazz : subTypes) {
            if (clazz.isAnnotationPresent(Path.class)) {
                String parentUri = clazz.getAnnotation(Path.class).value();
                parentUri = resolvePathToFormal(parentUri);
                Method[] methods = clazz.getMethods();
                for (Method m : methods) {
                    if (m.isAnnotationPresent(Path.class)) {
                        String uri = m.getAnnotation(Path.class).value();
                        uri = parentUri + resolvePathToFormal(uri);
                        map.put(m, uri);
                    }
                }
            }
        }

        return map;
    }

    public static Map<ResourceOverview, String> getResourceOverview(String... packageNames) {
        if (ArrayUtils.isEmpty(packageNames)) {
            return Collections.emptyMap();
        }

        Map<ResourceOverview, String> overviewMap = Maps.newHashMap();
        PackageNamesScanner scanner = new PackageNamesScanner(packageNames, true);
        AnnotationAcceptingListener listener = new AnnotationAcceptingListener(Path.class);

        scanClass(scanner, listener);

        Set<Class<?>> subTypes = listener.getAnnotatedClasses();
        for (Class<?> clazz : subTypes) {
            if (clazz.isAnnotationPresent(Path.class)) {
                String parentPath = clazz.getAnnotation(Path.class).value();
                parentPath = resolvePathToFormal(parentPath);
                ResourceOverview overview = clazz.isAnnotationPresent(ResourceOverview.class) ?
                        clazz.getAnnotation(ResourceOverview.class) : null;

                overviewMap.put(overview, parentPath);
            }
        }
        return overviewMap;
    }

    public static String loadTextFromFile(String templatePath) {
        InputStream in;
        in = ClassLoader.getSystemClassLoader().getResourceAsStream(templatePath);

        if (in == null) {
            in = ClassLoader.getSystemClassLoader().getResourceAsStream("/" + templatePath);
        }

        if (in == null) {
            in = ClassLoader.getSystemClassLoader().getResourceAsStream("/doclet/" + templatePath);
        }

        if (in == null) {
            try {
                in = new FileInputStream(templatePath);
            } catch (FileNotFoundException ex) {
                // TODO: Logger ???
            }
        }

        if (in == null) {
            return "";
        }

        return inputString2String(in);
    }

    public static Map<String, List<Method>> getResourceMethods(Map<Method, String> apiMap) {
        Map<String, List<Method>> methodMap = Maps.newHashMap();

        for (Method m : apiMap.keySet()) {
            Class dc = m.getDeclaringClass();
            Path pathAnnotation = (Path) dc.getAnnotation(Path.class);
            String parentPath = pathAnnotation.value();
            parentPath = resolvePathToFormal(parentPath);
            if (!methodMap.containsKey(parentPath)) {
                methodMap.put(parentPath, Lists.<Method>newArrayList());
            }

            List<Method> methodList = methodMap.get(parentPath);
            methodList.add(m);
        }

        return methodMap;
    }

    public static String prettyPrintJson(String json) {
        DumpJson dj = loadScriptProxy(JSON_FORMATTER, DumpJson.class);
        return dj.dumpJson(json).toString().trim();
    }

    public static <T extends Object> T loadScriptProxy(String path, Class<T> t) {
        T scriptProxy = null;
        try {
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("ECMAScript");
            String script = loadTextFromFile(path);
            scriptProxy = getScriptProxy(scriptEngine, script, t);
            return scriptProxy;
        } catch (ScriptException ex) {
            Logger.getLogger(RestapiDocTool.class.getName()).log(Level.SEVERE, null, ex);
        }
        return scriptProxy;
    }

    public static <T> T getScriptProxy(ScriptEngine ecmaScriptEnging, String script, Class<T> t) throws ScriptException {
        return ((Invocable) ecmaScriptEnging).getInterface(ecmaScriptEnging.eval("(" + script + ")"), t);
    }

    public interface DumpJson {
        public Object dumpJson(Object json);
    }

    public static String inputString2String(InputStream in) {
        StringBuilder sb = new StringBuilder();
        BufferedReader read = null;
        try {
            read = new BufferedReader(new InputStreamReader(in, Charsets.UTF_8));
            char[] ch = new char[1024];

            int d = read.read(ch);
            while (d != -1) {
                String str = new String(ch, 0, d);
                sb.append(str);
                d = read.read(ch);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RestapiDocTool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RestapiDocTool.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                read.close();
            } catch (IOException ex) {
                Logger.getLogger(RestapiDocTool.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return sb.toString();
    }
}
