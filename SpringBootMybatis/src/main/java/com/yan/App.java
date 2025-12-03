package com.yan;

import com.web.EnableHttpClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yan.mapper")
@EnableHttpClient
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }


}
