package com.spring.validation.example.request;

/**
 * @author fdrama
 * date 2023年05月17日 11:18
 */

import com.spring.validation.example.validator.AllowedValues;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class StudentRequest {
    @NotNull(message = "学号不能为空")
    @Size(min = 6, max = 10, message = "学号长度必须在6到10之间")
    private String id;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 20, message = "姓名长度不能超过20")
    private String name;

    @NotNull(message = "年龄不能为空")
    @Min(value = 18, message = "年龄不能小于18岁")
    @Max(value = 60, message = "年龄不能大于60岁")
    private Integer age;

    @NotNull(message = "性别不能为空")
    @AllowedValues(intValue = {0, 1}, message = "性别不正确")
    private Integer gender;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @DecimalMin(value = "0.0", inclusive = true, message = "成绩不能小于0")
    @DecimalMax(value = "100.0", inclusive = true, message = "成绩不能大于100")
    @Digits(integer = 3, fraction = 1, message = "成绩最多只能有3位整数和1位小数")
    private BigDecimal score;

    @Past(message = "入学时间必须是过去的时间")
    private Date admissionDate;

    @Future(message = "毕业时间必须是未来的时间")
    private Date graduationDate;

    @Positive(message = "班级人数必须是正整数")
    private Integer classSize;

    @Negative(message = "班级编号必须是负整数")
    private Integer classId;

    @PositiveOrZero(message = "学生数量不能为负数")
    private Integer studentCount;

    @NegativeOrZero(message = "教师数量不能为正数")
    private Integer teacherCount;

    // getters and setters
}

