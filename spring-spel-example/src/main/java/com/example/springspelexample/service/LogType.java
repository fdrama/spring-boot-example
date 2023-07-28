package com.example.springspelexample.service;

/**
 * @author fdrama
 * date 2023年07月27日 13:56
 */
public enum LogType {


    /**
     * 项目
     */
    PROJECT("PROJECT", "项目");

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
         * 项目
         */
        public static final String PROJECT = "PROJECT";

        /**
         * 任务
         */
        public static final String TASK = "TASK";

        /**
         * 人员
         */
        public static final String PERSON = "PERSON";

        /**
         * 部门
         */
        public static final String DEPARTMENT = "DEPARTMENT";

    }
}
