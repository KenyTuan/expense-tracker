package com.test.utils;

public class EnumUtils {
    public static <T extends Enum<T>> boolean isValidEnum(Class<T> enumClass, String value) {
        if (value == null) {
            return false;
        }
        try {
            Enum.valueOf(enumClass, value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
