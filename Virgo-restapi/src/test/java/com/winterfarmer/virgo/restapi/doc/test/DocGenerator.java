package com.winterfarmer.virgo.restapi.doc.test;

import com.winterfarmer.virgo.restapi.doc.generator.DocLauncher;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by yangtianhang on 15/5/6.
 */
@RunWith(JUnit4.class)
public class DocGenerator extends TestCase {
    @Test
    public void testGenerateDoc() {
        try {
            DocLauncher instance = new DocLauncher();
            instance.generateDoc();
        } catch (Throwable t) {
            fail("Document generating processMessageBody failed!  " + t.getMessage());
        }
    }

    @Test
    public void testRSA() {
    }
}
