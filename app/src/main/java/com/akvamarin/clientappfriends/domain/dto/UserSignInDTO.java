package com.akvamarin.clientappfriends.domain.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserSignInDTO implements Serializable {
    @SerializedName("id")
    private long id;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public UserSignInDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
