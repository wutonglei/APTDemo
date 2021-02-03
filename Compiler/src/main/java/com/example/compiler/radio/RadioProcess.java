package com.example.compiler.radio;

import com.example.annotation.BindRb;
import com.example.annotation.Factory;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;


/**
 * 1.生成RadioButtonListener
 */
@AutoService(Processor.class)
public class RadioProcess extends AbstractProcessor {
    public static int i = 0;
    public static int j = 0;
//    process()方法会调用3次，只有第一次有效，第2，3次调用的话生成.java文件会发生异常

    /**
     * 这个东西很迷 会执行多次
     * [com.example.annotation.BindRb]
     * 执行：0
     * []
     * 执行：1
     * []
     * 执行：2
     *
     * @param set              除了声明的所有类
     * set：[errorRaised=false, rootElements=[com.example.aptdemo.Circle, com.example.aptdemo.IShape, com.example.aptdemo.MainActivity, com.example.aptdemo.radiobutton.MainActivity_RG_FastRb, com.example.aptdemo.radiobutton.RadioButtonListener, com.example.aptdemo.Rectangle, com.example.aptdemo.TestFactory, com.example.aptdemo.BuildConfig], processingOver=false]
     * @param roundEnvironment 大概是所有类的集合（除了声明）  getSupportedAnnotationTypes 把[com.example.annotation.BindRb] 添加到roundEnvironment
     * <p>
     * roundEnvironment：[errorRaised=false, rootElements=[com.example.aptdemo.Circle, com.example.aptdemo.IShape, com.example.aptdemo.MainActivity, com.example.aptdemo.radiobutton.MainActivity_RG_FastRb, com.example.aptdemo.radiobutton.RadioButtonListener, com.example.aptdemo.Rectangle, com.example.aptdemo.TestFactory, com.example.aptdemo.BuildConfig], processingOver=false]
     * [com.example.annotation.BindRb]
     * @return 1.检测是不是同一个viewGroup下的 进行分组
     */

    private Map<Integer, List<VariableElement>> elementsMap = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.size() == 0)
            return true;
        //1、获取要处理的注解的元素的集合 获取所有BindRb.class：[button1, button2]
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindRb.class);
        System.out.println("获取所有BindRb.class：" + elements);
        System.out.println("set：" + roundEnvironment);
        System.out.println("roundEnvironment：" + roundEnvironment);


        System.out.println(set);
        System.out.println("执行：" + i);
        i++;

//        System.out.println("执行xxxx：" + element);
//        将统一viewGroup的元素整合在一起
        for (Element annotatedElement :
                elements) {
            VariableElement variableElement = (VariableElement) annotatedElement;
            if (ElementKind.FIELD != annotatedElement.getKind())
                continue;
            BindRb annotation = variableElement.getAnnotation(BindRb.class);


            List<VariableElement> variableElementList = elementsMap.get(annotation.groupId());
            if (variableElementList == null) {
                variableElementList = new ArrayList<VariableElement>();
            }
            variableElementList.add(variableElement);
            elementsMap.put(annotation.groupId(), variableElementList);


////返回此元素的修饰符，不包括注释。
//            annotatedElement.getModifiers();
////返回此元素的种类  ElementKind.FIELD
//            if (ElementKind.FIELD != annotatedElement.getKind())
//                continue;


            annotatedElement.getSimpleName();


//            Object constantValue = variableElement.getConstantValue();
////            返回此变量的包围元素。 方法或构造函数参数的封闭元素是声明参数的可执行文件。
////            enclosingElement:com.example.aptdemo.MainActivity
//            Element enclosingElement = variableElement.getEnclosingElement();
//            System.out.println(enclosingElement.getSimpleName());
////            MainActivity
//            Name simpleName = variableElement.getSimpleName();
//            System.out.println("constantValue:" + constantValue);
////            enclosingElement:com.example.aptdemo.MainActivity
//            System.out.println("enclosingElement:" + enclosingElement);
//            System.out.println("simpleName:" + simpleName);
//
//            System.out.println(annotatedElement.getKind());
//
//            generateCode(variableElement);

        }
        System.out.println("----elementsMap的大小"+elementsMap.size());
        for (List<VariableElement> veList : elementsMap.values()) {

            try {
                generateCode(veList);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            veList.clear();
        }
        elementsMap.clear();

        return true;
    }

    /**
     * 1.构建类名
     * 如何获取类名
     * Element enclosingElement = element.getEnclosingElement();
     * TypeElement superClassName = (TypeElement) enclosingElement;
     * PackageElement packageElement=elementUtils.getPackageOf(superClassName);
     * String pkName= packageElement.getQualifiedName().toString();
     * <p>
     * 如何获取声明 element.getAnnotation(Factory.class);
     * <p>
     * <p>
     * 如何获取View ？？？
     */
    private void generateCode(List<VariableElement> elementList) throws ClassNotFoundException {


        VariableElement element = elementList.get(0);

        Element enclosingElement = element.getEnclosingElement();
        BindRb annotation = element.getAnnotation(BindRb.class);


        System.out.println("----------" + enclosingElement);
        System.out.println("----------" + enclosingElement.getKind());
        System.out.println("----------" + enclosingElement.asType());
        System.out.println("----------" + enclosingElement.getClass());
        System.out.println("----------" + enclosingElement.getClass());
        TypeElement superClassName = (TypeElement) enclosingElement;
        PackageElement packageElement = elementUtils.getPackageOf(superClassName);
        String pkName = packageElement.getQualifiedName().toString();
        String activityName = enclosingElement.getSimpleName().toString();

//生成监听接口
        PackageElement pkg = elementUtils.getPackageOf(superClassName);
        String packageName = pkg.isUnnamed() ? null : pkg.getQualifiedName().toString();
        System.out.println("packageName:" + packageName);
        TypeSpec RadioButtonSelectedListener = TypeSpec.interfaceBuilder("RadioButtonSelectedListener")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(MethodSpec.methodBuilder("selected").addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build())
                .build();
        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC)
//                .addField(String.class, "greeting", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.methodBuilder("hey").addModifiers(Modifier.PUBLIC).build())
                .build();


        int groupId = annotation.groupId();
//        TypeName typeName=TypeName
        String clazzName = activityName + "RG" + groupId + "FastRb";
        TypeSpec.Builder fastRbBuilder = TypeSpec.classBuilder(clazzName)
                .addModifiers(Modifier.PUBLIC);

        String activity = "android.app.Activity";
        String radioButton = "android.widget.RadioButton";
        String radioGroup = "android.widget.RadioGroup";

//        Class activityClass=Class.forName(enclosingElement.getSimpleName().toString());

        String classNameStr = enclosingElement.getSimpleName().toString();
        System.out.println("-----" + classNameStr);
//        惊了  完全 就是猜测有类？？？    这个类实际上不存在即自己定义的在生成的文件能用到
//        这里用全类名不会报错 用简单的类名会报错后as自动导包
        ClassName parameterClassName = ClassName.bestGuess(classNameStr);
        ClassName radioButtonName = ClassName.bestGuess(radioButton);
        ClassName radioGroupName = ClassName.bestGuess(radioGroup);


//        TypeName typeName=new TypeName(activity);
//        fastRbBuilder.addField(typeName);
        MethodSpec.Builder bindBuilder = MethodSpec.methodBuilder("bind")

                .addParameter(parameterClassName, "activity");


        fastRbBuilder.addMethod(bindBuilder.build());
//        一个文件里面也就一个radioGroup
        fastRbBuilder.addField(radioGroupName, "radioGroup");
//        fastRbBuilder.addField();      int rg;
        FieldSpec rgFieldSpec = FieldSpec.builder(int.class, "rg")
                .initializer("$L", annotation.groupId())
                .build();

        fastRbBuilder.addField(rgFieldSpec);
        for (VariableElement variableElement : elementList) {
            System.out.println("variableElement:"+variableElement);
            annotation=  variableElement.getAnnotation(BindRb.class);
            int viewId = annotation.viewId();
            fastRbBuilder.addField(radioButtonName, variableElement.getSimpleName().toString());

            FieldSpec rbFieldSpec = FieldSpec.builder(int.class, "rb" + variableElement.getSimpleName().toString()+"Id")
                    .initializer("$L", annotation.viewId())
                    .build();
            fastRbBuilder.addField(rbFieldSpec);
//            获取变量名字
            String name = variableElement.getSimpleName().toString();
            String btnListenerName = name + "Listener";
//            我觉的是通过反射获取Class


//                Class clazz = Class.forName(packageName + "RadioButtonSelectedListener");
//            MethodSpec rbListenerMP = MethodSpec.constructorBuilder()
//                    .addModifiers(Modifier.PUBLIC)
//                    .returns(void.class)
////                        .addParameter(clazz, "set_rg" + groupId + "_" + name + "Listener")
//                    .addStatement(btnListenerName + " = radioButtonListener;")
//                    .build();
//            fastRbBuilder.addMethod(rbListenerMP);


        }


//        Attempt to recreate a file for type com.hannesdorfmann.annotationprocessing101.factory.MealFactory
//         构建已存在的文件导致的
        System.out.println("路过");
//        TypeSpec helloWorld = TypeSpec.interfaceBuilder("HelloWorld")
//                .addModifiers(Modifier.PUBLIC)
//                .addField(FieldSpec.builder(String.class, "ONLY_THING_THAT_IS_CONSTANT")
//                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
//                        .initializer("$S", "change")
//                        .build())
//                .addMethod(MethodSpec.methodBuilder("beep")
//                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
//                        .build())
//                .build();


//        TypeElement superClassName = elementUtils.getTypeElement("com.example.aptdemo.IShape");


/**
 *1.  怎么 通过element获取其所在类
 *
 */


        try {
            JavaFile.builder(packageName, RadioButtonSelectedListener).build().writeTo(filer);
            JavaFile.builder(packageName, helloWorld).build().writeTo(filer);
            JavaFile.builder(packageName, fastRbBuilder.build()).build().writeTo(filer);
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    /**
     * 返回所需要的注解类型  如果不返回则不处理
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
//
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(BindRb.class.getCanonicalName());
        System.out.println("执行j：" + j);
        System.out.println("getSupportedAnnotationTypes" + annotations);
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }

    Elements elementUtils;
    Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);


        elementUtils = processingEnvironment.getElementUtils();

        filer = processingEnvironment.getFiler();
    }


}
