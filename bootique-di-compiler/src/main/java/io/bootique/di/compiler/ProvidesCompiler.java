package io.bootique.di.compiler;

import io.bootique.di.Provides;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Processes {@link Provides} annotation, generating
 */
public class ProvidesCompiler extends AbstractProcessor {

    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (annotations.isEmpty()) {
            return true;
        }

        // TODO: can't use javax.annotation.processing.Messager with Maven for debug info. All, but ERROR and
        // MANDATORY_WARNING messages are swallowed and are not sent to the console..

        System.out.println("*** processing... " + annotations);

        Map<Name, ModuleExtensionsGenerator> extGenerators = new HashMap<>();

        for (TypeElement te : annotations) {

            Set<? extends Element> annotatedMethods = roundEnv.getElementsAnnotatedWith(te);
            for (Element e : annotatedMethods) {

                Element parentElement = e.getEnclosingElement();
                if (parentElement.getKind() != ElementKind.CLASS) {
                    return onError("Provider method's parent is not a class: " + parentElement);
                }

                TypeElement parentType = (TypeElement) parentElement;

                extGenerators
                        .computeIfAbsent(parentType.getQualifiedName(), n -> new ModuleExtensionsGenerator(parentType))
                        .addProviderMethod(e);
            }
        }


        extGenerators.values().forEach(this::storeProvider);

        return true;
    }

    private void storeProvider(ModuleExtensionsGenerator provider) {
        try {
            provider.file(filer);
        } catch (IOException e) {
            // TODO: break upstream processing after hard errors ... Use Monad with error state?
            onError("Can't print to file: " + e.getMessage());
        }
    }

    private boolean onError(String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message);
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(Provides.class.getName());
        return set;
    }
}
