package com.example.springspelexample.service;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author fdrama
 * date 2023年08月09日 11:33
 */
@NoArgsConstructor
@Data
public class User {

    private String userId;
    private String name;
    private String enName;
    private String nickname;
    private String email;
    private String mobile;
    private Integer gender;
    private String avatarKey;
    private List<String> departmentIds;
    private String city;

}
