package io.github.padago.springdocker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fdrama
 * date 2023年11月03日 11:34
 */
@RestController
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "Hello World!";
    }
}
