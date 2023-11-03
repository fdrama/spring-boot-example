package com.example.springspelexample.support.function;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;

/**
 * @author fdrama
 * date 2023年08月08日 17:26
 */
@Component
public class NowParseFunction implements IParseFunction {
    @Override
    public String functionName() {
        return "_NOW";
    }

    @Override
    public String apply(Object... value) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        if (value.length == 1 && StringUtils.hasText(value[0].toString())) {
            pattern = value[0].toString();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(System.currentTimeMillis());
    }
}
