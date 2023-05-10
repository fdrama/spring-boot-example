package com.example.controller.resp;

/**
 * @author fdrama
 * @date 2022年08月17日 16:53
 */
public interface GlobalError {

    /**
     * 获取错误码
     *
     * @return
     */
    default Integer getErrorCode() {
        return 0;
    }

    /**
     * 获取错误信息
     *
     * @return
     */
    default String getErrorMsg() {
        return "";
    }
}
