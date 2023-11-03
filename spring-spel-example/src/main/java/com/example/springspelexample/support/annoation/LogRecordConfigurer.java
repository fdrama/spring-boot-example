package com.example.springspelexample.support.annoation;

import com.example.springspelexample.support.config.LogRecordErrorHandler;
import com.example.springspelexample.support.config.LogRecordResolver;
import org.springframework.lang.Nullable;

/**
 * @author fdrama
 * date 2023年07月26日 17:34
 */
public interface LogRecordConfigurer {


    @Nullable
    default LogRecordResolver logResolver() {
        return null;
    }


    @Nullable
    default LogRecordErrorHandler errorHandler() {
        return null;
    }
}
