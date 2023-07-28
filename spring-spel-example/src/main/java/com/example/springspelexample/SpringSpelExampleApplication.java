package com.example.springspelexample;

import com.example.springspelexample.support.annoation.EnableLogRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableLogRecord
public class SpringSpelExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSpelExampleApplication.class, args);
    }

}
