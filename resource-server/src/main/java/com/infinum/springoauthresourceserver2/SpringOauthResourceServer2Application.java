package com.infinum.springoauthresourceserver2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
//        (exclude = { SecurityAutoConfiguration.class})
public class SpringOauthResourceServer2Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringOauthResourceServer2Application.class, args);
    }

}
