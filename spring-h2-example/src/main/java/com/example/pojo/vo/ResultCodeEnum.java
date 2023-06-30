package com.example.pojo.vo;

/**
 * @author fdrama
 * date 2023年06月30日 15:04
 */
public enum ResultCodeEnum {

    /**
     * 成功
     */
    SUCCESS(0, "success"),

    PARAM_ERROR(4001, "无效的userId"),

    ;
    private final int code;

    private final String msg;

    ResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
