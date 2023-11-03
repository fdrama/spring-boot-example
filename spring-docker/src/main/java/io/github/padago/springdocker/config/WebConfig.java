package io.github.padago.springdocker.config;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author fdrama
 * date 2023年11月03日 15:25
 */
@Configuration
@ServletComponentScan(value = {"io.github.padago.springdocker.filter"})
public class WebConfig {

}
