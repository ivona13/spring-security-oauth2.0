package com.infinum.authorizationserver.config;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OidcInfoUserService implements UserDetailsService {

    private final UserInfoRepository userInfoRepository = new UserInfoRepository();

    public AppUser loadUserByUsername(String username) {
        return this.userInfoRepository.findByUsername(username);
    }

    static class UserInfoRepository {

        private final Map<String, AppUser> userInfo = new HashMap<>();

        public UserInfoRepository() {
            this.userInfo.put("dev", new AppUser("dev", "{noop}dev", AppUser.getAuthorities("USER", "ADMIN"), "dev@email.com", "F"));
            this.userInfo.put("user", new AppUser("user", "{noop}password", AppUser.getAuthorities("USER"), "user@email.com", "F"));
        }

        public AppUser findByUsername(String username) {
            return this.userInfo.get(username);
        }
    }
}