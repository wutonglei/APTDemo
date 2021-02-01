package com.example.compiler.module;


import com.example.compiler.exception.IdAlreadyUsedException;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class FactoryGroupedClasses {
    /**
     * Will be added to the name of the generated factory class
     */
    private static final String SUFFIX = "Factory";
    private String qualifiedClassName;

    private Map<String, FactoryAnnotatedClass> itemsMap = new LinkedHashMap<>();

    public FactoryGroupedClasses(String qualifiedClassName) {
        this.qualifiedClassName = qualifiedClassName;
    }

    public void add(FactoryAnnotatedClass toInsert) {
        FactoryAnnotatedClass factoryAnnotatedClass = itemsMap.get(toInsert.getId());
        if (factoryAnnotatedClass != null) {
            throw new IdAlreadyUsedException(factoryAnnotatedClass);
        }
        itemsMap.put(toInsert.getId(), toInsert);
    }


    /**
     * 生成代码 先对照我们需要的代码例子
     *public class ShapeFactory {
     *   public Shape create(String id) {
     *     if (id == null) {
     *       throw new IllegalArgumentException("id is null!");
     *     }
     *     if ("Circle".equals(id)) {
     *       return new Circle();
     *     }
     *     if ("Rectangle".equals(id)) {
     *       return new Rectangle();
     *     }
     *     if ("Triangle".equals(id)) {
     *       return new Triangle();
     *     }
     *     throw new IllegalArgumentException("Unknown id = " + id);
     *   }
     * }
     *
     *
     * @param elementUtils
     * @param filer
     * @throws IOException
     */
    public void generateCode(Elements elementUtils, Filer filer) throws IOException {
        System.out.println("生成代码"+qualifiedClassName);
        TypeElement superClassName = elementUtils.getTypeElement(qualifiedClassName);
        String factoryClassName = superClassName.getSimpleName() + SUFFIX;
        String qualifiedFactoryClassName = qualifiedClassName + SUFFIX;
        PackageElement pkg = elementUtils.getPackageOf(superClassName);
        String packageName = pkg.isUnnamed() ? null : pkg.getQualifiedName().toString();

        MethodSpec.Builder method = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "id")
                .returns(TypeName.get(superClassName.asType()));

        // check if id is null
        method.beginControlFlow("if (id == null)")
                .addStatement("throw new IllegalArgumentException($S)", "id is null!")
                .endControlFlow();

        // Generate items map

        for (FactoryAnnotatedClass item : itemsMap.values()) {
            method.beginControlFlow("if ($S.equals(id))", item.getId())
                    .addStatement("return new $L()", item.getTypeElement().getQualifiedName().toString())
                    .endControlFlow();
        }

        method.addStatement("throw new IllegalArgumentException($S + id)", "Unknown id = ");

        TypeSpec typeSpec = TypeSpec
                .classBuilder(factoryClassName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(method.build())
                .build();

        // Write file
        JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
    }
}
