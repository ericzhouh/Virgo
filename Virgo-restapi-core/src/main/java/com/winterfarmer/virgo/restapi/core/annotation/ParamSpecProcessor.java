package com.winterfarmer.virgo.restapi.core.annotation;

import com.winterfarmer.virgo.restapi.core.param.AbstractParamSpec;
import com.winterfarmer.virgo.restapi.core.param.ParamSpecFactory;
import org.apache.commons.collections4.set.ListOrderedSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * Created by yangtianhang on 15-2-27.
 */
//@AutoService(Processor.class)
public class ParamSpecProcessor extends AbstractProcessor {
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "yes in init(ProcessingEnvironment processingEnv)");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.ERROR, "yes in process");
        return true;
//        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(ParamSpec.class)) {
//            if (annotatedElement.getKind() != ElementKind.METHOD) {
//                error(annotatedElement, "Only methods can be annotated with @%s", ParamSpec.class.getSimpleName());
//                return true;
//            }
//
//            ExecutableElement executableElement = (ExecutableElement) annotatedElement;
//            ParamSpec annotation = executableElement.getAnnotation(ParamSpec.class);
//            if (!isValidDesc(annotation.desc())) {
//                error(annotatedElement, "Invalid desc of @%s", ParamSpec.class.getSimpleName());
//                return false;
//            }
//        }
//
//        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "yes in getSupportedSourceVersion");
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "yes in getSupportedAnnotationTypes");

        Set<String> annotations = new ListOrderedSet<>();
        annotations.add(ParamSpec.class.getCanonicalName());
        return annotations;
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, String.format(msg, args), e);
    }

    private boolean isValidDesc(String desc) {
        try {
            AbstractParamSpec<?> spec = ParamSpecFactory.getParamSpec(desc);
            return spec.isValid(desc);
        } catch (Exception e) {
            return false;
        }
    }
}
