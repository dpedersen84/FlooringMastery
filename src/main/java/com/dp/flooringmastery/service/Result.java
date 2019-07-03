package com.dp.flooringmastery.service;

public class Result<T> extends Response {

    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
