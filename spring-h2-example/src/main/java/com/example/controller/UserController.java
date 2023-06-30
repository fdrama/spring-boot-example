package com.example.controller;

import com.example.pojo.vo.PageResult;
import com.example.pojo.vo.user.UserVO;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author fdrama
 * date 2023年06月30日 11:13
 */
@RestController()
@RequestMapping("/user")
public class UserController {


    private UserService userService;

    @GetMapping("/{id}")
    public UserVO get(@PathVariable(name = "id") Long id) {
        return userService.findOne(id);
    }

    @GetMapping("/queryAll")
    public List<UserVO> queryAll() {
        return userService.queryAll();
    }

    @GetMapping("/query")
    public PageResult<UserVO> queryAll(@RequestParam(name = "pageNum", defaultValue = "0") Integer pageNum, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        return userService.pageQuery(pageNum, pageSize);
    }

    @PostMapping
    public UserVO create(@RequestBody UserVO user) {
        return userService.add(user);
    }

    @PutMapping
    public UserVO update(@Validated(value = UserVO.UpdateUser.class) @RequestBody UserVO user) {
        return userService.update(user);
    }

    @PatchMapping
    public UserVO patch(@Validated(value = UserVO.UpdateUser.class) @RequestBody UserVO user) {
        return userService.patch(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable(name = "id") Long id) {
        userService.delete(id);
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
