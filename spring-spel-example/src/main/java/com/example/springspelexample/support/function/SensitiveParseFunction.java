package com.example.springspelexample.support.function;

import org.springframework.stereotype.Component;

/**
 * @author fdrama
 * date 2023年08月08日 17:26
 */
@Component
public class SensitiveParseFunction implements IParseFunction {
    @Override
    public String functionName() {
        return "_SENSITIVE";
    }

    @Override
    public String apply(Object... value) {
        // 第一个参数是需要脱敏的字符串，第二个参数是脱敏类型 支持邮箱 手机号 .....
        value[0] = value[0].toString().trim();
        if (value.length == 2) {
            switch (value[1].toString()) {
                case "EMAIL":
                    return email(value[0].toString());
                case "PHONE":
                    return phone(value[0].toString());
                default:
                    break;
            }
        }
        return value[0].toString();
    }

    private String phone(String toString) {
        if (toString.length() == 11) {
            return toString.substring(0, 3) + "****" + toString.substring(7);
        }
        return toString;
    }

    private String email(String toString) {
        String[] split = toString.split("@");
        if (split.length == 2) {
            String prefix = split[0];
            String suffix = split[1];
            if (prefix.length() > 3) {
                prefix = prefix.substring(0, 3) + "***";
            }
            return prefix + "@" + suffix;
        }
        return toString;
    }
}
