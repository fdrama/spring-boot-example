package com.example.pojo.vo.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author fdrama
 * date 2023年06月30日 15:34
 */
@Data
public class UserVO {

    @NotNull(message = "id不能为空", groups = UpdateUser.class)
    private Long id;
    private String name;
    private Integer age;

    public interface UpdateUser {
    }
}
