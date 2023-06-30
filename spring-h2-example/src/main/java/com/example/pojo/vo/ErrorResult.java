package com.example.pojo.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author fdrama
 * date 2023年06月30日 15:15
 */
public class ErrorResult<T> extends BaseResult<T> {

    private ErrorResult(){

    }

    public static <T> ErrorResult<T> fail(int code, String msg) {
        ErrorResult<T> result = new ErrorResult<>();
        result.setData(null);
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Override
    public T getData() {
        return super.getData();
    }
}
