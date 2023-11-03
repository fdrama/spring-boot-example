package com.example.springspelexample.support.function;

/**
 * @author fdrama
 * date 2023年07月28日 17:15
 */
public interface IFunctionService {

    /**
     * apply function
     *
     * @param functionName
     * @param value
     * @return
     */
    String apply(String functionName, Object... value);

}
