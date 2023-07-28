package com.example.springspelexample.support.annoation;

import org.springframework.util.Assert;

/**
 * @author fdrama
 * date 2023年07月26日 16:37
 */

public class LogRecordOperation {

    private String content;
    private String type;
    private String subType;
    private String bizNo;
    private String extra;
    private String operator;
    private String condition;

    private String operatorGetter;

    private String logResolver;

    private String errorHandler;

    public String getErrorHandler() {
        return errorHandler;
    }

    public String getLogResolver() {
        return logResolver;
    }

    public String getContent() {
        return content;
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

    public String getOperatorGetter() {
        return operatorGetter;
    }

    public String getCondition() {
        return condition;
    }

    private String toString;

    @Override
    public final String toString() {
        return this.toString;
    }

    protected LogRecordOperation(Builder b) {

        this.content = b.content;
        this.type = b.type;
        this.subType = b.subType;
        this.bizNo = b.bizNo;
        this.extra = b.extra;
        this.operator = b.operator;
        this.condition = b.condition;
        this.operatorGetter = b.operatorGetter;
        this.logResolver = b.logResolver;
        this.errorHandler = b.errorHandler;
        this.toString = b.getOperationDescription().toString();

    }

    public static class Builder {
        // required parameters
        private String content = "";
        private String type = "";
        private String subType = "";
        private String bizNo;
        private String extra = "";
        private String operator;
        private String condition = "";

        private String operatorGetter = "";

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

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            Assert.hasText(content, "content must not be empty");
            this.content = content;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            Assert.hasText(type, "type must not be empty");

            this.type = type;
        }

        public String getSubType() {
            return subType;
        }

        public void setSubType(String subType) {
            this.subType = subType;
        }

        public String getBizNo() {
            return bizNo;
        }

        public void setBizNo(String bizNo) {
            Assert.hasText(bizNo, "bizNo must not be empty");
            this.bizNo = bizNo;
        }

        public String getExtra() {
            return extra;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getCondition() {
            return condition;
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

        public void setOperatorGetter(String operatorGetter) {
            this.operatorGetter = operatorGetter;
        }

        public String getOperatorGetter() {
            return operatorGetter;
        }

        protected StringBuilder getOperationDescription() {
            StringBuilder result = new StringBuilder(getClass().getSimpleName());
            result.append("[");
            result.append("content=").append(content);
            result.append(", type=").append(type);
            result.append(", subType=").append(subType);
            result.append(", bizNo=").append(bizNo);
            result.append(", extra=").append(extra);
            result.append(", operator=").append(operator);
            result.append(", condition=").append(condition);
            result.append(", operatorGetter=").append(operatorGetter);
            result.append("]");
            return result;
        }

        public LogRecordOperation build() {
            return new LogRecordOperation(this);
        }


    }
}
