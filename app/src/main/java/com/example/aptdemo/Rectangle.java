package com.example.aptdemo;

import com.example.annotation.Factory;

@Factory(id="Rectangle", type=IShape.class)
public class Rectangle implements IShape {
    @Override
    public void draw() {
        System.out.println("Draw a Rectangle");
    }
}
