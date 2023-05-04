package com.akvamarin.clientappfriends.domain.dto;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class AuthUserParamDTO {

    @NotNull
    private String username;

    @NotNull
    private String password;
}
