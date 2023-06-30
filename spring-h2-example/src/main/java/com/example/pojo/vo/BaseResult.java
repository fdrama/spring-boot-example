package com.example.pojo.vo;

import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author fdrama
 * @date 2022年08月19日 13:41
 */
@NoArgsConstructor
public class BaseResult<T> implements Serializable {

    private static final long serialVersionUID = 4399312326569765281L;
    private int code;

    private String msg;

    private T data;

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
