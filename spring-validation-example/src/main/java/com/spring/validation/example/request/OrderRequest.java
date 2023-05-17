package com.spring.validation.example.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author fdrama
 * date 2023年05月17日 14:34
 */
@Data
public class OrderRequest {

    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @NotNull(message = "商品列表不能为空")
    @Size(min = 1, message = "商品列表不能为空")
    @Valid
    private List<ProductRequest> productList;

    @Valid
    @NotNull(message = "收货地址不能为空")
    private AddressRequest address;

}
