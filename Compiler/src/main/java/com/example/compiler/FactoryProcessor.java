package com.example.compiler;

import com.example.annotation.Factory;
import com.example.compiler.exception.ProcessingException;
import com.example.compiler.module.FactoryAnnotatedClass;
import com.example.compiler.module.FactoryGroupedClasses;
import com.google.auto.service.AutoService;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
//https://blog.csdn.net/qq_20521573/article/details/82321755
//注意 要修改才能重新生成类  不然是不会重新生成的  也不会进入到process方法中
/**
 * 为了生成合乎要求的ShapeFactory类，
 * 在生成ShapeFactory代码前需要对被Factory注解的元素进行一系列的校验，只有通过校验，
 * 符合要求了才可以生成ShapeFactory代码。根据需求，我们列出如下规则：
 * <p>
 * 1.只有类才能被@Factory注解。因为在ShapeFactory中我们需要实例化Shape对象，
 * 虽然@Factory注解声明了Target为ElementType.TYPE，但接口和枚举并不符合我们的要求。
 * 2.被@Factory注解的类中需要有public的构造方法，这样才能实例化对象。
 * 3.被注解的类必须是type指定的类的子类
 * 4.id需要为String类型，并且需要在相同type组中唯一
 * 5.具有相同type的注解类会被生成在同一个工厂类中
 */
/**
 * 生成代码的方式
 * 1.通过filer来手写 手拼
 * 2.通过JavaPoet 来  JavaPoet可以用对象的方式来帮助我们生成类代码
 */


//，它的作用是用来生成META-INF/services/javax.annotation.processing.Processor文件的，
// 也就是我们在使用注解处理器的时候需要手动添加META-INF/services/javax.annotation.processing.Processor，
// 而有了@AutoService后它会自动帮我们生成
@AutoService(Processor.class)
public class FactoryProcessor extends AbstractProcessor {
    private Types typeUtils;
    private Messager messager;
    private Filer filer;
    private Elements elementUtils;
    private Map<String, FactoryGroupedClasses> factoryClasses = new LinkedHashMap<>();

    /**
     * 这个方法用于初始化处理器，方法中有一个ProcessingEnvironment类型的参数，ProcessingEnvironment是一个注解处理工具的集合。它包含了众多工具类。例如：
     * Filer可以用来编写新文件；
     * Messager可以用来打印错误信息；
     * Elements是一个可以处理Element的工具类。
     *
     * @param processingEnvironment 在这里我们有必要认识一下什么是Element
     *                              在Java语言中，Element是一个接口，表示一个程序元素，它可以指代包、类、方法或者一个变量。
     *                              Element已知的子接口有如下几种：
     *                              PackageElement 表示一个包程序元素。提供对有关包及其成员的信息的访问。
     *                              ExecutableElement 表示某个类或接口的方法、构造方法或初始化程序（静态或实例），包括注释类型元素。
     *                              TypeElement 表示一个类或接口程序元素。提供对有关类型及其成员的信息的访问。注意，枚举类型是一种类，而注解类型是一种接口。
     *                              VariableElement 表示一个字段、enum 常量、方法或构造方法参数、局部变量或异常参数。
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        elementUtils = processingEnvironment.getElementUtils();
    }


    /**
     * package com.zhpan.mannotation.factory;  //    PackageElement
     * <p>
     * public class Circle {  //  TypeElement
     * <p>
     * private int i; //   VariableElement
     * private Triangle triangle;  //  VariableElement
     * <p>
     * public Circle() {} //    ExecuteableElement
     * <p>
     * public void draw(   //  ExecuteableElement
     * String s)   //  VariableElement
     * {
     * System.out.println(s);
     * }
     * <p>
     * public void draw() {    //  ExecuteableElement
     * System.out.println("Draw a circle");
     * }
     * }
     */


//这个方法非常简单，只有一个返回值，用来指定当前正在使用的Java版本，
// 通常return SourceVersion.latestSupported()即可。
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }

    //    这个方法的返回值是一个Set集合，集合中指要处理的注解类型的名称(这里必须是完整的包名+类名，
//    例如com.example.annotation.Factory)。由于在本例中只需要处理@Factory注解，因此Set集合中只需要添加@Factory的名称即可。
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Factory.class.getCanonicalName());
        return annotations;
//        return super.getSupportedAnnotationTypes();
    }


    //在这个方法的方法体中，我们可以校验被注解的对象是否合法、可以编写处理注解的代码，
// 以及自动生成需要的java文件等。因此说这个方法是AbstractProcessor 中的最重要的一个方法。
// 我们要处理的大部分逻辑都是在这个方法中完成。
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
//        返回值，是一个boolean类型，返回值表示注解是否由当前Processor 处理。
//        如果返回 true，则这些注解由此注解来处理，后续其它的 Processor 无需再处理它们；
//        如果返回 false，则这些注解未在此Processor中处理并，那么后续 Processor 可以继续处理它们。
        try {
            // Scan classes
            for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(Factory.class)) {

                // Check if a class has been annotated with @Factory
                //  // 检查被注解为@Factory的元素是否是一个类
                if (annotatedElement.getKind() != ElementKind.CLASS) {
                    throw new ProcessingException(annotatedElement, "Only classes can be annotated with @%s",
                            Factory.class.getSimpleName());
                }

                // We can cast it, because we know that it of ElementKind.CLASS
                TypeElement typeElement = (TypeElement) annotatedElement;

                FactoryAnnotatedClass annotatedClass = new FactoryAnnotatedClass(typeElement);

                checkValidClass(annotatedClass);

                // Everything is fine, so try to add   ？？？？？  是为了将包名相同的类放到一起？？？？  感觉有点想太多
                FactoryGroupedClasses factoryClass = factoryClasses.get(annotatedClass.getQualifiedFactoryGroupName());
                if (factoryClass == null) {
                    String qualifiedGroupName = annotatedClass.getQualifiedFactoryGroupName();
                    factoryClass = new FactoryGroupedClasses(qualifiedGroupName);
                    factoryClasses.put(qualifiedGroupName, factoryClass);
                }

                // Checks if id is conflicting with another @Factory annotated class with the same id
                factoryClass.add(annotatedClass);
            }
/**
 * 生成代码的方式
 * 1.通过filer来手写 手拼
 * 2.通过JavaPoet 来  JavaPoet可以用对象的方式来帮助我们生成类代码
 */
            // Generate code
            for (FactoryGroupedClasses factoryClass : factoryClasses.values()) {
                factoryClass.generateCode(elementUtils, filer);
            }
            factoryClasses.clear();
        } catch (ProcessingException e) {
            error(e.getElement(), e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void checkValidClass(FactoryAnnotatedClass item) throws ProcessingException {

        // Cast to TypeElement, has more type specific methods
        TypeElement classElement = item.getTypeElement();

        if (!classElement.getModifiers().contains(Modifier.PUBLIC)) {
            throw new ProcessingException(classElement, "The class %s is not public.",
                    classElement.getQualifiedName().toString());
        }

        // Check if it's an abstract class
        if (classElement.getModifiers().contains(Modifier.ABSTRACT)) {
            throw new ProcessingException(classElement,
                    "The class %s is abstract. You can't annotate abstract classes with @%",
                    classElement.getQualifiedName().toString(), Factory.class.getSimpleName());
        }

//          // 这个类必须是在@Factory.type()中指定的类的子类，否则抛出异常终止编译
        // Check inheritance: Class must be child class as specified in @Factory.type();
        TypeElement superClassElement = elementUtils.getTypeElement(item.getQualifiedFactoryGroupName());
        if (superClassElement.getKind() == ElementKind.INTERFACE) {
            // Check interface implemented
            // 检查被注解类是否实现或继承了@Factory.type()所指定的类型，此处均为IShape
            if (!classElement.getInterfaces().contains(superClassElement.asType())) {
                throw new ProcessingException(classElement,
                        "The class %s annotated with @%s must implement the interface %s",
                        classElement.getQualifiedName().toString(), Factory.class.getSimpleName(),
                        item.getQualifiedFactoryGroupName());
            }
        } else {
            // Check subclassing
            TypeElement currentClass = classElement;
            while (true) {
                /**
                 * getSuperclass()
                 * Returns the direct superclass of this type element.
                 * If this type element represents an interface or the class java.lang.Object,
                 * then a NoType with kind NONE is returned.
                 */
                TypeMirror superClassType = currentClass.getSuperclass();

                if (superClassType.getKind() == TypeKind.NONE) {
                    // Basis class (java.lang.Object) reached, so exit
                    throw new ProcessingException(classElement,
                            "The class %s annotated with @%s must inherit from %s",
                            classElement.getQualifiedName().toString(), Factory.class.getSimpleName(),
                            item.getQualifiedFactoryGroupName());
                }

                if (superClassType.toString().equals(item.getQualifiedFactoryGroupName())) {
                    // Required super class found
                    break;
                }

                // Moving up in inheritance tree
                currentClass = (TypeElement) typeUtils.asElement(superClassType);
            }
        }

        // Check if an empty public constructor is given
        // 检查是否由public的无参构造方法
        for (Element enclosed : classElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructorElement = (ExecutableElement) enclosed;
                if (constructorElement.getParameters().size() == 0 &&
                        constructorElement.getModifiers().contains(Modifier.PUBLIC)) {
                    // Found an empty constructor
                    return;
                }
            }
        }

        // No empty constructor found
        throw new ProcessingException(classElement,
                "The class %s must provide an public empty default constructor",
                classElement.getQualifiedName().toString());
    }


    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private void error(String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    private void info(String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }
}