package io.bootique.di.compiler;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import io.bootique.di.Binder;
import io.bootique.di.Module;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ModuleExtensionsGenerator {

    private TypeElement moduleType;
    private Set<Element> providerMethods;

    public ModuleExtensionsGenerator(TypeElement moduleType) {
        this.moduleType = moduleType;
        this.providerMethods = new HashSet<>();
    }

    public void addProviderMethod(Element e) {
        providerMethods.add(e);
    }

    public void file(Filer filer) throws IOException {

        // generate an extra module that registers all "@Provides" methods as providers.

        String className = moduleType.getSimpleName().toString();
        String packageName = ((PackageElement) moduleType.getEnclosingElement()).getQualifiedName().toString();

        MethodSpec mConfigure = MethodSpec
                .methodBuilder("configure")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(Binder.class, "binder")
                .build();

        // TODO: provider inner classes and bindings for them in 'configure(..)'

        AnnotationSpec aGenerated = AnnotationSpec
                .builder(Generated.class)
                .addMember("value", "$S", "io.bootique.di.compiler")
                .build();

        TypeSpec extClass = TypeSpec
                .classBuilder("_" + className + "_ExtModule")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(Module.class)
                .addAnnotation(aGenerated)
                .addMethod(mConfigure)
                .build();

        JavaFile javaFile = JavaFile
                .builder(packageName, extClass)
                // set Idea-like indentation with 4 spaces
                .indent("    ")
                .build();

        javaFile.writeTo(filer);
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder("[").append(moduleType.getQualifiedName());

        for (Element e : providerMethods) {
            buffer.append(" ").append(e.getSimpleName());
        }

        return buffer.append("]").toString();
    }
}
