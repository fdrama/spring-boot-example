package com.example.springspelexample;

import com.example.springspelexample.service.Inventor;
import com.example.springspelexample.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@SpringBootTest
class SpringSpelExampleApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
    }

    // Expression templating
    @Test
    void testExpressionTemplating() {
        ExpressionParser parser = new SpelExpressionParser();
        String randomPhrase =
                parser.parseExpression("random number is #{T(java.lang.Math).random()}",
                        new TemplateParserContext()).getValue(String.class);
        Assertions.assertNotNull(randomPhrase);
    }

    @Test
    void testUser() {
        userService.updateUser(new Inventor("tom", "us"));
    }

}
