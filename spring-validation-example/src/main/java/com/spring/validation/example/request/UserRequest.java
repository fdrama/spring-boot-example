package com.spring.validation.example.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.GroupSequence;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.groups.Default;

/**
 * @author fdrama
 * date 2023年05月17日 14:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @NotEmpty(message = "姓氏不能为空")
    private String firstName;

    @NotEmpty(message = "名字不能为空")
    private String lastName;

    @NotEmpty(message = "邮箱不能为空")
    @Email(message = "邮箱地址不合法", groups = UserEmailGroup.class)
    private String email;

    @NotEmpty(message = "手机号码不能为空", groups = UserEmailGroup.class)
    private String phone;

    public interface UserEmailGroup{
    }

    public interface UserPhoneGroup{

    }

    @GroupSequence({Default.class, UserEmailGroup.class, UserPhoneGroup.class})
    public interface UserGroup {

    }

}
