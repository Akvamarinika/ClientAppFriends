package com.akvamarin.clientappfriends.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class UserSignInDTO implements Serializable {
    @JsonProperty("id")
    private long id;

    @JsonProperty("email")
    private final String email;

    @JsonProperty("password")
    private final String password;

    public UserSignInDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
