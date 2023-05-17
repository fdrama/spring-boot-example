package com.spring.validation.example;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Map;

/**
 * @author fdrama
 * date 2023年05月17日 11:25
 */
@Data
public class ValidationResult {
    public static final String SPLIT_CHAR = "\r\n";

    private boolean hasErrors;

    private Map<String, String> errorMsg;

    public String getMessage() {
        if (errorMsg == null || errorMsg.isEmpty()) {
            return "";
        }
        StringBuilder message = new StringBuilder();

        errorMsg.forEach((key, value) -> {
            message.append(MessageFormat.format("{0}: {1} \r\n", key, value));
        });
        String result = message.toString();
        if (StringUtils.hasText(message) && message.lastIndexOf(SPLIT_CHAR) != -1) {
            return result.substring(0, message.lastIndexOf(SPLIT_CHAR));
        }
        return result;
    }
}
