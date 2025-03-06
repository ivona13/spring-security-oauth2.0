package com.infinum.authorizationserver.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class AppUser extends User {

    private String email;

    private String gender;

    public AppUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public AppUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String email, String gender) {
        this(username, password, authorities);
        this.email = email;
        this.gender = gender;
    }


    public AppUser withEmail(String email) {
        this.email = email;
        return this;
    }

    public AppUser withGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getEmail() {
        return this.email;
    }

    public String getGender() {
        return this.gender;
    }

    public static List<SimpleGrantedAuthority> getAuthorities(String... roles) {
        return Stream.of(roles)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
