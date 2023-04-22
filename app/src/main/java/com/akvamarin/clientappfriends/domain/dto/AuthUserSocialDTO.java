package com.akvamarin.clientappfriends.domain.dto;

import com.akvamarin.clientappfriends.domain.enums.Role;
import com.akvamarin.clientappfriends.domain.enums.Sex;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthUserSocialDTO implements Serializable {

    private String username;

    private String email;

    @NotNull
    public String vkId;

    @NotNull
    private String socialToken;

    private String firstName;

    private String lastName;

    private String photo;

    private String dateOfBirth;

    private String city;

    private String country;

    private Sex sex;

    @NotNull
    private Set<Role> roles;
}
