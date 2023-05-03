package com.akvamarin.clientappfriends.domain.dto;

import com.akvamarin.clientappfriends.domain.enums.Alcohol;
import com.akvamarin.clientappfriends.domain.enums.Psychotype;
import com.akvamarin.clientappfriends.domain.enums.Role;
import com.akvamarin.clientappfriends.domain.enums.Sex;
import com.akvamarin.clientappfriends.domain.enums.Smoking;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {

    private Long id;

    private String email;

    private String username;

    private String phone;

    private String password;

    private String dateOfBirthday;

    private String nickname;

    private Sex sex;

    private String aboutMe;

    private Smoking smoking;

    private Alcohol alcohol;

    private Psychotype psychotype;

    private String urlAvatar;

    private Long cityID;

    private Set<Role> roles;

    private String vkId;

}
