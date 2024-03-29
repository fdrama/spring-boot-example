package com.example.simplemybatisdemo.dao.enums;

public enum Gender implements BaseCodeEnum{

    MALE(1, "男"),
    FEMALE(2, "女"),
    UNKNOWN(0, "未知");

    private final int code;

    private final String name;

    Gender(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }


}
