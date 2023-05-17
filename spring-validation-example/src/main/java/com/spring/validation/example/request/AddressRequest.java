package com.spring.validation.example.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author fdrama
 * date 2023年05月17日 14:35
 */
@Data
public class AddressRequest {

    @NotEmpty(message = "地区code不能为空")
    private String addressCode;

    @NotEmpty(message = "地址详情不能为空")
    private String addressDetail;
}
