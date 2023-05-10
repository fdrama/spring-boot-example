package com.example.controller;

import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fdrama
 * date 2023年05月10日 14:59
 */
@RestController
@RequestMapping("user")
public class UserController {

    private UserService userService;



    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
