package com.example.controller.resp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author fdrama
 * date 2023年05月10日 17:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBO implements Serializable {

    private Integer userId;

    private String username;
}
