package com.test.utils.converter;

public interface Converter<T> {
    String toJson(T t);
    T fromJson(String json);
}
