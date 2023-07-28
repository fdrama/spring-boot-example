package com.example.springspelexample.support.aop;

import org.springframework.lang.Nullable;

/**
 * @author fdrama
 * date 2023年07月26日 17:59
 */
@FunctionalInterface
public interface LogRecordOperationInvoker {

    @Nullable
    Object invoke() throws LogRecordOperationInvoker.ThrowableWrapper;


    /**
     * Wrap any exception thrown while invoking {@link #invoke()}.
     */
    @SuppressWarnings("serial")
    class ThrowableWrapper extends RuntimeException {

        private final Throwable original;

        public ThrowableWrapper(Throwable original) {
            super(original.getMessage(), original);
            this.original = original;
        }

        public Throwable getOriginal() {
            return this.original;
        }
    }
}
