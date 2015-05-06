package com.winterfarmer.virgo.restapi.doc.generator;

/**
 * Created by yangtianhang on 15/4/26.
 */
public class DocLauncher {
    public static void generateDoc() throws Throwable {
        DocGenerator.doClean();
//        new ErrorPageGenerator().run();
//        new HomePageGenerator().run();
        new ModelPageGenerator().run();
    }

    public static void main(String[] args) throws Throwable {
        generateDoc();
    }
}