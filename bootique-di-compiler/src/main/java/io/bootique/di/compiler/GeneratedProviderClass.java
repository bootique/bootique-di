package io.bootique.di.compiler;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

public class GeneratedProviderClass {

    private TypeElement moduleType;
    private Set<Element> providerMethods;

    public GeneratedProviderClass(TypeElement moduleType) {
        this.moduleType = moduleType;
        this.providerMethods = new HashSet<>();
    }

    public void addProviderMethod(Element e) {
        providerMethods.add(e);
    }

    public void file(Filer filer) throws IOException {

        String className = moduleType.getSimpleName() + "_Provider";
        String fqClassName = moduleType.getQualifiedName() + "_Provider";

        JavaFileObject file = filer.createSourceFile(fqClassName);
        try (Writer w = file.openWriter()) {
            w.append("package foo; class " + className + " {}");
        }
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
