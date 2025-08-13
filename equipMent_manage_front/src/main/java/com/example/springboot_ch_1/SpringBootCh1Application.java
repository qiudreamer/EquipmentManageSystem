package com.example.springboot_ch_1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class
})
public class SpringBootCh1Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCh1Application.class, args);
    }

}
