package com.example.springspelexample.support.annoation;

import org.springframework.util.Assert;

/**
 * @author fdrama
 * date 2023年07月26日 16:37
 */

public class LogRecordOperation {

    private final String successTemplate;

    private final String failTemplate;
    private final String type;
    private final String subType;
    private final String bizNo;
    private final String extra;
    private final String operator;
    private final String condition;

    private final String successCondition;

    private final String logResolver;

    private final String errorHandler;

    public String getErrorHandler() {
        return errorHandler;
    }

    public String getLogResolver() {
        return logResolver;
    }

    public String getFailTemplate() {
        return failTemplate;
    }

    public String getSuccessTemplate() {
        return successTemplate;
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public String getBizNo() {
        return bizNo;
    }

    public String getExtra() {
        return extra;
    }

    public String getOperator() {
        return operator;
    }

    public String getCondition() {
        return condition;
    }

    public String getSuccessCondition() {
        return successCondition;
    }

    private String toString;

    @Override
    public final String toString() {
        return this.toString;
    }

    protected LogRecordOperation(Builder b) {

        this.successTemplate = b.successTemplate;
        this.failTemplate = b.failTemplate;
        this.type = b.type;
        this.subType = b.subType;
        this.bizNo = b.bizNo;
        this.extra = b.extra;
        this.operator = b.operator;
        this.condition = b.condition;
        this.successCondition = b.successCondition;
        this.logResolver = b.logResolver;
        this.errorHandler = b.errorHandler;
        this.toString = b.getOperationDescription().toString();
    }

    public static class Builder {
        // required parameters
        private String successTemplate = "";

        private String failTemplate = "";
        private String type = "";
        private String subType = "";
        private String bizNo;
        private String extra = "";
        private String operator;
        private String condition = "";

        private String successCondition = "";

        private String name = "";

        private String logResolver = "";

        private String errorHandler = "";

        public String getLogResolver() {
            return logResolver;
        }

        public String getErrorHandler() {
            return errorHandler;
        }

        public void setErrorHandler(String errorHandler) {
            this.errorHandler = errorHandler;
        }

        public void setLogResolver(String logResolver) {
            this.logResolver = logResolver;
        }

        public void setSuccessTemplate(String successTemplate) {
            this.successTemplate = successTemplate;
        }

        public void setSuccessCondition(String successCondition) {
            this.successCondition = successCondition;
        }

        public void setFailTemplate(String failTemplate) {
            this.failTemplate = failTemplate;
        }

        public void setType(String type) {
            Assert.hasText(type, "type must not be empty");
            this.type = type;
        }


        public void setSubType(String subType) {
            this.subType = subType;
        }


        public void setBizNo(String bizNo) {
            Assert.hasText(bizNo, "bizNo must not be empty");
            this.bizNo = bizNo;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }


        public void setOperator(String operator) {
            this.operator = operator;
        }


        public void setCondition(String condition) {
            this.condition = condition;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }


        protected StringBuilder getOperationDescription() {
            StringBuilder result = new StringBuilder(getClass().getSimpleName());
            result.append("[");
            result.append("successTemplate=").append(successTemplate);
            result.append(", failTemplate=").append(failTemplate);
            result.append(", type=").append(type);
            result.append(", subType=").append(subType);
            result.append(", bizNo=").append(bizNo);
            result.append(", extra=").append(extra);
            result.append(", operator=").append(operator);
            result.append(", condition=").append(condition);
            result.append(", successCondition=").append(successCondition);
            result.append("]");
            return result;
        }

        public LogRecordOperation build() {
            return new LogRecordOperation(this);
        }


    }
}
