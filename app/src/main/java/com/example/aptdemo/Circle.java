package com.example.aptdemo;

import com.example.annotation.Factory;

@Factory(id="Circle", type=IShape.class)
public class Circle implements IShape {
    @Override
    public void draw() {
        System.out.println("Draw a Circle");
    }
}
