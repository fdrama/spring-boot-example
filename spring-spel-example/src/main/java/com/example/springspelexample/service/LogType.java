package com.example.springspelexample.service;

/**
 * @author fdrama
 * date 2023年07月27日 13:56
 */
public enum LogType {


    /**
     * 部门
     */
    DEPARTMENT(Constants.DEPARTMENT, "部门"),

    USER(Constants.USER, "用户"),
    ;

    private final String typeCode;
    private final String typeDesc;

    LogType(String typeCode, String typeDesc) {
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
        /**
         * 部门
         */
        public static final String DEPARTMENT = "DEPARTMENT";
        /**
         * 用户
         */
        public static final String USER = "USER";


    }
}
