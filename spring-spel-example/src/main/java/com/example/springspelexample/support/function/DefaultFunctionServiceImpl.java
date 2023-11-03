package com.example.springspelexample.support.function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author fdrama
 * date 2023年07月28日 17:15
 */
public class DefaultFunctionServiceImpl implements IFunctionService {

    protected final Log logger = LogFactory.getLog(getClass());

    private final ParseFunctionFactory parseFunctionFactory;

    public DefaultFunctionServiceImpl(ParseFunctionFactory parseFunctionFactory) {
        this.parseFunctionFactory = parseFunctionFactory;
    }

    @Override
    public String apply(String functionName, Object... value) {
        IParseFunction function = parseFunctionFactory.getFunction(functionName);
        if (function == null) {
            logger.warn("function not found: " + functionName);
            return null;
        }
        return function.apply(value);
    }

}
