package com.infinum.springoauthresourceserver2.web;

import com.infinum.springoauthresourceserver2.util.AuthContextUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public endpoint";
    }

    @GetMapping("/greetings")
    public String privateEndpoint() {
        String authenticatedUser = AuthContextUtil.getUser();
        return "Greetings from " + authenticatedUser;
    }
}
