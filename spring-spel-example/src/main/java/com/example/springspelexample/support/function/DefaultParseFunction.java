package com.example.springspelexample.support.function;

import java.util.Arrays;

/**
 * @author fdrama
 * date 2023年07月28日 17:14
 */
public class DefaultParseFunction implements IParseFunction {

    @Override
    public String functionName() {
        return "DEFAULT";
    }

    @Override
    public String apply(Object... value) {
        return Arrays.toString(value);
    }


}
