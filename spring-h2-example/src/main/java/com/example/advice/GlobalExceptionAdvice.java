package com.example.advice;


import com.example.pojo.vo.BaseResult;
import com.example.pojo.vo.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.pojo.vo.ResultCodeEnum.PARAM_ERROR;

/**
 * 全局异常处理切面对错误异常统一包装处理
 *
 * @author fdrama
 * @date 2022年08月17日 16:33
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {


    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResult<Object> illegalArgumentExceptionHandle(IllegalArgumentException e) {
        return ErrorResult.fail(PARAM_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResult<Object> methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();

        List<Map<String, String>> list = getErrorMsgList(allErrors);

        return ErrorResult.fail(PARAM_ERROR.getCode(), list + "");
    }

    private List<Map<String, String>> getErrorMsgList(List<ObjectError> allErrors) {
        List<Map<String, String>> list = new ArrayList<>();
        for (ObjectError objectError : allErrors) {
            Map<String, String> errorMap = new HashMap<>(allErrors.size());
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                errorMap.put("field", fieldError.getField());
                errorMap.put("message", fieldError.getDefaultMessage());
            } else {
                errorMap.put("field", objectError.getObjectName());
                errorMap.put("message", objectError.getDefaultMessage());
            }
            list.add(errorMap);
        }
        return list;
    }
}
