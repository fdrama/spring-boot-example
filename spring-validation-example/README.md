#  validation

## 名称解释

### JSR-303

JSR-303是Java规范请求（Java Specification Request）的一部分，它定义了Java Bean验证的标准API。它提供了一种简单的方法来验证Java Bean中的字段，以确保它们符合特定的规则和约束。这些规则可以是必填字段、最小值、最大值、正则表达式等。JSR-303的实现可以用于各种Java应用程序，包括Web应用程序、桌面应用程序和移动应用程序。

## 基础注解使用

### 内置constraint

```java
package javax.validation.constraints
```

- @NotNull：用于检查属性是否为 null。
- @Size：用于检查字符串属性的长度是否在指定范围内。
- @Min 和 @Max：用于检查数字属性是否在指定范围内。
- @DecimalMin 和 @DecimalMax：用于检查 BigDecimal 和 BigInteger 属性是否在指定范围内。
- @Digits：用于检查数字属性的整数和小数部分的位数是否符合要求。
- @Positive 和 @PositiveOrZero：用于检查数字属性是否为正数或非负数。
- @Negative 和 @NegativeOrZero：用于检查数字属性是否为负数或非正数。
- @Past 和 @Future：用于检查日期属性是否是过去或未来的日期。
- @Pattern：用于检查字符串属性是否符合指定的正则表达式。

## 自定义注解

虽然基础注解的支持`Pattern`可以完成大部分场景的验证，但是有时候复杂的正则表达式没有代码表达清晰，也不容易维护，往往需要自定义注解。有时候我们需要约定传值必须是在某些特殊值范围内，可以按照以下方式自定义。

我们定义了一个名为 `AllowedValues` 的注解，它有两个属性：value 和 intValue。value 属性用于指定允许出现的字符串值，intValue 属性用于指定允许出现的整数值。我们还使用了 `@Constraint` 注解来指定注解的校验器类 `AllowedValuesValidator`。

```java
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {AllowedValuesValidator.class})
public @interface AllowedValues {
    String message() default "参数值不在允许的范围内";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] value() default {};
    int[] intValue() default {};
}
```

校验器类 AllowedValuesValidator 需要实现 ConstraintValidator 接口，并重写 initialize 和 isValid 方法，例如：

```java
public class AllowedValuesValidator implements ConstraintValidator<AllowedValues, Object> {
    private String[] allowedValues;
    private int[] allowedIntValues;

    @Override
    public void initialize(AllowedValues annotation) {
        this.allowedValues = annotation.value();
        this.allowedIntValues = annotation.intValue();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            String strValue = (String) value;
            for (String allowedValue : allowedValues) {
                if (allowedValue.equals(strValue)) {
                    return true;
                }
            }
        } else if (value instanceof Integer) {
            int intValue = (int) value;
            for (int allowedIntValue : allowedIntValues) {
                if (allowedIntValue == intValue) {
                    return true;
                }
            }
        }
        return false;
    }
}
```

## 级联校验

级联校验是指在校验一个对象时，需要对该对象中的某些属性进行校验。例如，我们需要对一个订单进行校验，但是订单中包含了多个商品，每个商品又包含了商品名称、商品价格等属性，我们需要对每个商品的属性进行校验，才能保证订单的正确性。

```java
public class OrderRequest {
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @NotNull(message = "商品列表不能为空")
    @Size(min = 1, message = "商品列表不能为空")
    @Valid
    private List<ProductRequest> productList;

    // getters and setters
}

public class ProductRequest {
    @NotBlank(message = "商品名称不能为空")
    private String name;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于 0")
    private BigDecimal price;

    // getters and setters
}
```

通过使用 `@Valid` 注解，我们可以实现级联校验，从而保证了对象中的所有属性的正确性。

## 分组校验

有时候同一个实体类，由于使用场景不同（新增，修改），对同一参数的校验逻辑可能不同，

假设我们正在开发一个社交网站，用户可以发布动态，但是发布的动态需要进行校验，例如：动态内容不能为空，动态内容不能超过 140 个字符等。同时，用户可以在发布动态时选择是否公开，如果选择公开，则需要对动态的可见性进行校验，例如：如果用户选择公开，则需要对动态的可见范围进行校验，如果用户选择不公开，则不需要进行可见性校验。就可以使用分组校验

首先我们定义两个分组，例如

```java
public interface PublicPostGroup {}

public interface PrivatePostGroup {}
```

在 PublicPostGroup 分组中，我们需要对动态内容和动态的可见性进行校验；在 PrivatePostGroup 分组中，我们只需要对动态内容进行校验。

接下来，我们需要定义发布动态请求的实体类，例如：

```java
public class PostRequest {
    @NotBlank(message = "动态内容不能为空", groups = {PublicPostGroup.class, PrivatePostGroup.class})
    @Size(max = 140, message = "动态内容不能超过 140 个字符", groups = {PublicPostGroup.class, PrivatePostGroup.class})
    private String content;

    @NotNull(message = "动态可见性不能为空", groups = {PublicPostGroup.class})
    private Boolean isPublic;

    @NotEmpty(message = "动态可见范围不能为空", groups = {PublicPostGroup.class})
    private List<Long> visibleUserIds;

    // getters and setters
}
```

在使用分组校验时，我们需要在 Controller 层的方法参数上添加 @Validated 注解，并指定需要校验的分组，例如：

```java
@RestController
public class PostController {
    @PostMapping("/post")
    public ResponseEntity<String> post(@Validated({PublicPostGroup.class, PrivatePostGroup.class}) @RequestBody PostRequest request) {
        // 处理发布动态
        return ResponseEntity.ok("动态发布成功");
    }

    @PostMapping("/public-post")
    public ResponseEntity<String> publicPost(@Validated(PublicPostGroup.class) @RequestBody PostRequest request) {
        // 处理发布公开动态
        return ResponseEntity.ok("公开动态发布成功");
    }
}
```

## 分组有序校验 @GroupSequence

默认情况下，不同组的校验顺序是无序的，但有些场景下，我们想控制分组校验的顺序，比如某个组的约束依赖于第一个约束执行完成的结果，比如某个分组校验非常耗时，我们想将它放在最后校验，这时候就可以使用`@GroupSequence`，定义分组校验的顺序，如果前面的分组校验失败，则后面的分组校验不会执行。

例如手机号和邮箱的校验由于复杂的规则，放在默认的非空校验后，当邮箱为空字符串的时候不再进行邮箱的合法性校验

```java
public class User {

    @NotEmpty(message = "姓氏不能为空")
    private String firstName;

    @NotEmpty(message = "名字不能为空")
    private String lastName;

    @NotEmpty(message = "邮箱不能为空")
    @Email(message = "邮箱地址不合法", groups = UserEmailGroup.class)
    private String email;

    @NotEmpty(message = "手机号码不能为空", groups = UserEmailGroup.class)
    private String phone;

    public interface UserEmailGroup {
    }

    public interface UserPhoneGroup {

    }
    
    @GroupSequence({Default.class, UserEmailGroup.class, UserPhoneGroup.class})
    public interface UserGroup {

    }

}

```

## 动态分组校验 @GroupSequenceProvider

在实际的业务场景中，往往存在更复杂的校验逻辑，例如B属性的校验依赖于A属性的值。例如合同创建的时候，合同期限类型为固定期限的时候，才去校验合同有效期的开始日期和结束日期，否则不进行校验。应该怎么做呢。`@GroupSequenceProvider` 可以解决这个问题。

我们同样开始定义实体类，并且使用`@GroupSequenceProvider` 指定处理器

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@GroupSequenceProvider(ContractGroupSequenceProvider.class)
public class ContractRequest {

    @NotBlank(message = "合同名称不能为空")
    private String contractName;

    /**
     * 固定期限编码
     * - 0：无固定期限
     * - 1：固定期限
     */
    @AllowedValues(intValue = {0, 1})
    @NotNull(message = "固定期限编码不合法")
    private Integer fixedValidityCode;

    /**
     * 开始日期 yyyy-MM-dd
     */
    @NotEmpty(groups = WhenFixedValidityCodeIsOk.class, message = "固定期限开始日期不能为空")
    @DateFormat(groups = WhenNeedCheckDate.class)
    private String startDate;
    /**
     * 结束日期 yyyy-MM-dd
     */
    @NotEmpty(groups = WhenFixedValidityCodeIsOk.class, message = "固定期限结束日期不能为空")
    @DateFormat(groups = WhenNeedCheckDate.class)
    private String endDate;

    /**
     * 固定期限校验
     */
    public interface WhenFixedValidityCodeIsOk   {

    }

    public interface WhenNeedCheckDate   {

    }
}
```

实现 `DefaultGroupSequenceProvider` ，接口中定义了一个 `getValidationGroups` 方法，用于返回需要参与分组校验的分组。在实现该接口时，我们可以根据不同的条件返回不同的分组，从而动态地指定分组校验的顺序。

```java
public class ContractGroupSequenceProvider implements DefaultGroupSequenceProvider<ContractRequest> {

    @Override
    public List<Class<?>> getValidationGroups(ContractRequest param) {
        List<Class<?>> defaultGroupSequence = new ArrayList<>();
        defaultGroupSequence.add(ContractRequest.class);
        
        if (Objects.nonNull(param)) {
            // 固定期限 校验开始日期，结束日期不能为空
            if (param.getFixedValidityCode() != null && param.getFixedValidityCode() == 1) {
                defaultGroupSequence.add(ContractRequest.WhenFixedValidityCodeIsOk.class);
            }
            // 开始日期不为空 校验日期合法性
            if (param.getFixedValidityCode() == 1 && StringUtils.hasText(param.getStartDate())){
                defaultGroupSequence.add(ContractRequest.WhenNeedCheckDate.class);
            }
            // 结束日期不为空 校验日期合法性
            if (param.getFixedValidityCode() == 1 && StringUtils.hasText(param.getEndDate())){
                defaultGroupSequence.add(ContractRequest.WhenNeedCheckDate.class);
            }
        }
        return defaultGroupSequence;
    }
}
```

需要注意的时候，动态分组校验也是`短路`校验，前一个分组校验失败，不会进行下一个分组校验

## 校验快速失败

spring 默认的参数校验是全部校验，可以通过设置为`短路`校验，来提升效率

```java
@Configuration
public class ParamValidatorConfig {

    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(true)
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
        methodValidationPostProcessor.setValidator(validator());
        return methodValidationPostProcessor;
    }

}
```

## 统一异常处理

spring 参数校验失败后的报错信息，可以通过统一异常处理捕获，返回信息。

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder sb = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getDefaultMessage()).append("; ");
        }
        return ResponseEntity.badRequest().body(sb.toString());
    }
}
```

## 国际化信息处理
