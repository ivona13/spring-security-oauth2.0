package com.infinum.authorizationserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CustomOidcTokenCustomizer {

    private final UserDetailsService userDetailsService;

    public CustomOidcTokenCustomizer(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
                String username = context.getPrincipal().getName();

                CustomUserDetails customUserDetails = (CustomUserDetails) this.userDetailsService.loadUserByUsername(username);
                Map<String, Object> customClaims = new HashMap<>();
                customClaims.put("email", customUserDetails.getEmail());
                customClaims.put("gender", customUserDetails.getGender());

                context.getClaims().claims(claims -> claims.putAll(customClaims));
            }
        };
    }
}