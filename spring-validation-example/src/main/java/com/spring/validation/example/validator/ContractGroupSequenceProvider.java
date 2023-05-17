package com.spring.validation.example.validator;

import com.spring.validation.example.request.ContractRequest;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author fdrama
 * date 2023年05月17日 15:04
 */

public class ContractGroupSequenceProvider implements DefaultGroupSequenceProvider<ContractRequest> {

    @Override
    public List<Class<?>> getValidationGroups(ContractRequest param) {
        List<Class<?>> defaultGroupSequence = new ArrayList<>();
        defaultGroupSequence.add(ContractRequest.class);
        if (Objects.nonNull(param)) {

            if (param.getFixedValidityCode() != null && param.getFixedValidityCode() == 1) {
                defaultGroupSequence.add(ContractRequest.WhenFixedValidityCodeIsOk.class);
            }
            if (param.getFixedValidityCode() == 1 && StringUtils.hasText(param.getStartDate())) {
                defaultGroupSequence.add(ContractRequest.WhenNeedCheckDate.class);
            }
            if (param.getFixedValidityCode() == 1 && StringUtils.hasText(param.getEndDate())) {
                defaultGroupSequence.add(ContractRequest.WhenNeedCheckDate.class);
            }
        }
        return defaultGroupSequence;
    }
}
