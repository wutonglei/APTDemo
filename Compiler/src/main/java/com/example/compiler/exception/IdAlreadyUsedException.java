package com.example.compiler.exception;


import com.example.compiler.module.FactoryAnnotatedClass;

public class IdAlreadyUsedException extends RuntimeException {
    public IdAlreadyUsedException(FactoryAnnotatedClass factoryAnnotatedClass) {

    }
}
