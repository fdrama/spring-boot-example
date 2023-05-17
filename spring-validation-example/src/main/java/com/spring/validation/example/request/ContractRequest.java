package com.spring.validation.example.request;

import com.spring.validation.example.ContractGroupSequenceProvider;
import com.spring.validation.example.validator.AllowedValues;
import com.spring.validation.example.validator.DateFormat;
import com.spring.validation.example.validator.DateRange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.group.GroupSequenceProvider;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

;

/**
 * @author fdrama
 * date 2023年05月17日 14:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@GroupSequenceProvider(ContractGroupSequenceProvider.class)
public class ContractRequest {

    @NotBlank(message = "合同名称不能为空")
    private String contractName;


    @Valid
    @DateRange
    private TimeRangeRequest timeRangeRequest;
    /**
     * 固定期限编码
     * - 0：无固定期限
     * - 1：固定期限
     */
    @AllowedValues(intValue = {0, 1})
    @NotNull(message = "固定期限编码不合法")
    private Integer fixedValidityCode;

    /**
     * 开始日期 yyyy-MM-dd
     */
    @NotEmpty(groups = WhenFixedValidityCodeIsOk.class, message = "固定期限开始日期不能为空")
    @DateFormat(groups = WhenNeedCheckDate.class)
    private String startDate;
    /**
     * 结束日期 yyyy-MM-dd
     */
    @NotEmpty(groups = WhenFixedValidityCodeIsOk.class, message = "固定期限结束日期不能为空")
    @DateFormat(groups = WhenNeedCheckDate.class)
    private String endDate;

    /**
     * 固定期限
     */
    public interface WhenFixedValidityCodeIsOk {

    }


    public interface WhenNeedCheckDate {

    }
}
