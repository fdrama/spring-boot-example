package com.example.simplemybatisdemo.dao.enums;

public class CodeEnumUtil {
    /**
     * 根据 code 获取枚举
     */
    public static <E extends Enum<?> & BaseCodeEnum> E codeOf(Class<E> enumClass, int code) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E e : enumConstants) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }
}
