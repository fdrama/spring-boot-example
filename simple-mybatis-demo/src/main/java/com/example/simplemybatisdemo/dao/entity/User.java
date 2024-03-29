package com.example.simplemybatisdemo.dao.entity;

import com.example.simplemybatisdemo.dao.enums.Gender;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String avatar;
    private Gender gender;
    private Date birthday;
    private Date createdAt;
    private Date updatedAt;

    // 构造函数、getter、setter 方法
    // ...
}