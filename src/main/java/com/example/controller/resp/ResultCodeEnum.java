package com.example.controller.resp;

/**
 * @author fdrama
 * @date 2022年08月17日 16:46
 */
public enum ResultCodeEnum implements GlobalError {

    /**
     * 返回状态码 未知
     */
    SUCCESS(0, "success"),

    UNKNOWN_ERROR(-1, "系统错误，请联系系统管理员！"),

    PARAM_ERROR(-2, "参数异常，请检查请求参数！"),


    MEDIA_TYPE_ERROR(-3, "请求体异常，请检查请求Media Type"),

    METHOD_ERROR(-4, "请求方法错误，请检查请求方法！"),
    ;


    private final int code;

    private final String msg;

    ResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }
}
