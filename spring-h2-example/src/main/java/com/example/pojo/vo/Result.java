package com.example.pojo.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author fdrama
 * @date 2022年08月17日 16:34
 */
@Getter
@Setter
public class Result<T> extends BaseResult<T> {

    private static final long serialVersionUID = 2496896790562451893L;

    private Result() {

    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
        return result;
    }

    public static <T> Result<T> fail(int code, String msg) {
        Result<T> result = new Result<>();
        result.setData(null);
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> fail(int code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setData(null);
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
