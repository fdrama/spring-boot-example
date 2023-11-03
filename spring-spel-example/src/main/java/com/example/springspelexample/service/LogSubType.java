package com.example.springspelexample.service;

/**
 * @author fdrama
 * date 2023年07月27日 13:56
 */
public enum LogSubType {


    /**
     * 新增
     */
    ADD(Constants.ADD, "新增"),

    UPDATE(Constants.UPDATE, "更新"),
    ;

    private final String typeCode;
    private final String typeDesc;

    LogSubType(String typeCode, String typeDesc) {
        this.typeCode = typeCode;
        this.typeDesc = typeDesc;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public static class Constants {

        public static final String ADD = "ADD";

        public static final String UPDATE = "UPDATE";

        public static final String DELETE = "DELETE";

        public static final String QUERY = "QUERY";

    }
}
