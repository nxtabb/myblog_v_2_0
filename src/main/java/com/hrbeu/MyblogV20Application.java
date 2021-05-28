package com.hrbeu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan(value = "com.hrbeu.dao")
@EnableTransactionManagement
public class MyblogV20Application {

    public static void main(String[] args) {
        SpringApplication.run(MyblogV20Application.class, args);
    }

}
