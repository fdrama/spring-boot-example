package com.spring.validation.example.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author fdrama
 * date 2023年05月17日 16:50
 */
@Data
@AllArgsConstructor
public class TimeRangeRequest {

    private Date startDate;

    private Date endDate;
}
