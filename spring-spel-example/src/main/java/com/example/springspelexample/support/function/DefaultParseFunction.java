package com.example.springspelexample.support.function;

/**
 * @author fdrama
 * date 2023年07月28日 17:14
 */
public class DefaultParseFunction implements IParseFunction {

    @Override
    public String functionName() {
        return "default";
    }

    @Override
    public String apply(Object value) {
        return value.toString();
    }

    @Override
    public boolean executeBefore() {
        return false;
    }
}
