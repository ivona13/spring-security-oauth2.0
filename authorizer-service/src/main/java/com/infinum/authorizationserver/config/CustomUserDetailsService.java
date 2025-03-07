package com.infinum.authorizationserver.config;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserInfoRepository userInfoRepository = new UserInfoRepository();

    public CustomUserDetails loadUserByUsername(String username) {
        return this.userInfoRepository.findByUsername(username);
    }

    static class UserInfoRepository {

        private final Map<String, CustomUserDetails> userInfo = new HashMap<>();

        public UserInfoRepository() {
            this.userInfo.put("dev", new CustomUserDetails("dev", "{noop}dev", CustomUserDetails.getAuthorities("USER", "ADMIN"), "dev@email.com", "F"));
            this.userInfo.put("user", new CustomUserDetails("user", "{noop}password", CustomUserDetails.getAuthorities("USER"), "user@email.com", "F"));
        }

        public CustomUserDetails findByUsername(String username) {
            return this.userInfo.get(username);
        }
    }
}