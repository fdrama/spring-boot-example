package com.example.controller.advice;

import com.example.controller.resp.BaseResult;
import com.example.controller.resp.PageResult;
import com.example.controller.resp.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashSet;
import java.util.Set;

/**
 * @author fdrama
 * @date 2022年08月17日 16:33
 */
@RestControllerAdvice
@Slf4j
public class ResultResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private static final Set<Class<?>> NOT_SUPPORT_CLASSES = new HashSet<>(6);

    static {
        NOT_SUPPORT_CLASSES.add(PageResult.class);
        NOT_SUPPORT_CLASSES.add(BaseResult.class);
        NOT_SUPPORT_CLASSES.add(Result.class);
        NOT_SUPPORT_CLASSES.add(byte[].class);
        NOT_SUPPORT_CLASSES.add(Resource.class);
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !NOT_SUPPORT_CLASSES.contains(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (returnType.getMethodAnnotation(IgnoreConvertAnnotation.class) != null) {
            return body;
        }
        return Result.success(body);
    }
}
