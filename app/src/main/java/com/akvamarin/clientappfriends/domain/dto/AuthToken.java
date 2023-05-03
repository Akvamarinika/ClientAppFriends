package com.akvamarin.clientappfriends.domain.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AuthToken {
    private String token;
    private String tokenType = "Bearer";

    @JsonCreator
    public AuthToken(@JsonProperty("token") String token) {
        this.token = token;
    }
}
