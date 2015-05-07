package com.winterfarmer.virgo.restapi.doc.generator;

import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by yangtianhang on 15/4/26.
 */
public abstract class DocGenerator {
    public static final String SEP = "/";
    public static String NEWLINE = "\n";
    public static final String WIN32 = "windows";

    static {
        Properties props = System.getProperties();
        String osName = props.getProperty("os.name");
        if (osName.toLowerCase().contains(WIN32)) {
            NEWLINE = "\r\n";
        } else {
            NEWLINE = "\n";
        }
    }

    public static final String docBasePropKey = "com.winterfarmer.virgo.docbase";
    public static final String defaultDocBase = System.getProperty(docBasePropKey) != null ?
            System.getProperty(docBasePropKey) : System.getProperty("user.home") + "/workspace/virgo.wiki";

    public static String TEMPLATE_BASE = "doc";

    public static final String HOME_PAGE_PATH = defaultDocBase + "/Home.md";
    public static final String API_DOC_DIR = defaultDocBase + "/apidocs";
    public static final String ERROR_DOC_PATH = defaultDocBase + "/base/error_code_info.md";
    public static final String API_MODEL_PATH = defaultDocBase + "/base/api_json.md";

    public static void doClean() throws Exception {
        try {
            Files.deleteIfExists(FileSystems.getDefault().getPath(HOME_PAGE_PATH));
            FileUtils.deleteDirectory(new File(API_DOC_DIR));
            FileUtils.deleteQuietly(new File(ERROR_DOC_PATH));
            FileUtils.deleteQuietly(new File(API_MODEL_PATH));
        } catch (IOException ex) {
            Logger.getLogger(DocGenerator.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }


    public void run() throws Throwable {
        Map<String, String> param = prepare();
        String template = readTemplateAsString(getTemplatePath());
        String docContent = renderingTemplateWithParam(param, template);
        System.out.println(getDocPath());
        saveDocument(new File(getDocPath()), docContent);
        postActions();
    }

    /**
     * prepare text to be filled in the template, and resolve the templatePath
     * and docPath.
     *
     * @return
     * @throws java.lang.Throwable
     */
    public abstract Map<String, String> prepare() throws Throwable;

    public abstract String getTemplatePath();

    public abstract String getDocPath();

    public String readTemplateAsString(String templatePath) {
        return RestapiDocTool.loadTextFromFile(templatePath);
    }

    public String renderingTemplateWithParam(Map<String, String> param, String template) {
        for (Map.Entry<String, String> e : param.entrySet()) {
            System.out.println("before: " + template);
            template = template.replace(e.getKey(), e.getValue());
            System.out.println("after: " + template);
        }

        return template;
    }

    public void saveDocument(File file, String content) throws Exception {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(DocGenerator.class.getName()).log(Level.SEVERE, null, ex);
                throw ex;
            }
        }

        try (final PrintStream out = new PrintStream(file, Charsets.UTF_8.displayName())) {
            out.println(content);
            out.flush();
        } catch (Exception ex) {
            Logger.getLogger(DocGenerator.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public abstract void postActions();
}
