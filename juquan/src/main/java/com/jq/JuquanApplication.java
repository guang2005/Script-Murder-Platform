package com.jq;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.jq.mapper")
public class JuquanApplication {

    public static void main(String[] args) {
        SpringApplication.run(JuquanApplication.class, args);
    }

}
