package com.akvamarin.clientappfriends.domain.dto;


import lombok.Data;

@Data
public class AuthToken {
    private String token;
    private String tokenType = "Bearer";

    public AuthToken(String token) {
        this.token = token;
    }
}
