package com.hrbeu.conf;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname BeanConfig
 * @Description TODO
 * @Date 2021/5/20 10:40
 * @Created by nxt
 */

@Configuration
public class BeanConfig {
    @Bean
    public ErrorProperties errorProperties() {
        return new ErrorProperties();
    }
}
