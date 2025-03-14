package com.product.uptime.dto;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

