package com.example.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//作用于类
//    TYPE,
//    FIELD,
//    METHOD,
//    PARAMETER,
//    CONSTRUCTOR,
//    LOCAL_VARIABLE,
//    ANNOTATION_TYPE,
//    PACKAGE,
//    TYPE_PARAMETER,
//    TYPE_USE;
@Target(ElementType.TYPE)


//编译时注解   SOURCE, CLASS, RUNTIME;
@Retention(RetentionPolicy.CLASS)
public @interface Factory {
    Class type();
    String id();
}
