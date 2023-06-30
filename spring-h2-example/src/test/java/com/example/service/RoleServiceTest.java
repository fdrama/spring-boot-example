package com.example.service;

import com.example.entity.Role;
import com.example.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author fdrama
 * date 2023年05月12日 15:33
 */
@SpringBootTest
@Slf4j
public class RoleServiceTest {


    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void test() {

        Role role = new Role();
        role.setRoleName("root");
        role.setStatus((byte) 0);
        Role save = roleRepository.save(role);
        System.out.println(save);
    }
}
