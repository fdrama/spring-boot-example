package com.example.springspelexample.service;

import org.springframework.stereotype.Service;

/**
 * @author fdrama
 * date 2023年07月27日 14:15
 */
@Service
public class UserServiceImpl implements UserService{


    @Override
    public void updateUser(Inventor inventor) {
        System.out.println("update user");
    }
}
