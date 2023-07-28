package com.example.springspelexample.support.function;

/**
 * @author fdrama
 * date 2023年07月28日 9:22
 */
public interface IParseFunction {

    String functionName();

    String apply(Object value);

    boolean executeBefore();
}
