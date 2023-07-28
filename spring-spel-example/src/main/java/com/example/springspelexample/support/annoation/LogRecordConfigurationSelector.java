package com.example.springspelexample.support.annoation;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fdrama
 * date 2023年07月26日 16:16
 */
public class LogRecordConfigurationSelector extends AdviceModeImportSelector<EnableLogRecord> {

    @Override
    protected String[] selectImports(AdviceMode adviceMode) {
        switch (adviceMode) {
            case PROXY:
                return getProxyImports();
            case ASPECTJ:
                return getAspectJImports();
            default:
                return null;
        }
    }

    private String[] getAspectJImports() {
        return new String[] {ProxyLogRecordConfiguration.class.getName()};
    }

    private String[] getProxyImports() {
        List<String> result = new ArrayList<>(3);
        result.add(AutoProxyRegistrar.class.getName());
        result.add(ProxyLogRecordConfiguration.class.getName());
        return StringUtils.toStringArray(result);
    }
}
