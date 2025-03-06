package com.infinum.springoauthresourceserver2.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

public class AuthContextUtil {

    private static Map<String, Object> extractClaim() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        return ((Jwt) principal).getClaims();
    }

    public static String getUser() {
        return (String) extractClaim().get("sub");
    }
}
