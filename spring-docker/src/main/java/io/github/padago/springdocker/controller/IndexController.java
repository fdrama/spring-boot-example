package io.github.padago.springdocker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fdrama
 * date 2023年11月03日 11:34
 */
@RestController
@Slf4j
public class IndexController {

    @GetMapping("/")
    public String index() {
        log.info("Hello World!");
        return "Hello World!";
    }
}
