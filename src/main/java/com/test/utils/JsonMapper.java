package com.test.utils;

import com.test.utils.converter.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

public class JsonMapper<T> {
    private final Converter<T> converter;

    public JsonMapper(Converter<T> converter) {
        this.converter = converter;
    }

    public String toJson(List<T> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        return items.stream()
                .map(converter::toJson)
                .collect(joining(",\n","[\n","\n]"));
    }

    public List<T> fromJson(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }

        String jsonContent = json.substring(1, json.length() - 1).trim();
        if (jsonContent.isEmpty()) {
            return new ArrayList<>();
        }

        String[] jsonObjects = jsonContent.split("},\\s*\\{");

        return Arrays.stream(jsonObjects)
                .map(obj -> {
                    if (!obj.startsWith("{")) {
                        obj = "{" + obj;
                    }
                    if (!obj.endsWith("}")) {
                        obj = obj + "}";
                    }
                    return converter.fromJson(obj);
                })
                .collect(Collectors.toList());
    }
}
