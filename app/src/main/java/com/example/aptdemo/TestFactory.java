package com.example.aptdemo;

import android.graphics.drawable.shapes.Shape;

public class TestFactory {

    public IShape create(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null!");
        }
        if ("Circle".equals(id)) {
//            return new Circle();
        }
        if ("Rectangle".equals(id)) {
            return new Rectangle();
        }
        if ("Triangle".equals(id)) {
//            return new Triangle();
        }
        throw new IllegalArgumentException("Unknown id = " + id);
    }


}
