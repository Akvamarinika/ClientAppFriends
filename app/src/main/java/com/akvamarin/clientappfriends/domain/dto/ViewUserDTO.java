package com.akvamarin.clientappfriends.domain.dto;

import com.akvamarin.clientappfriends.domain.enums.Alcohol;
import com.akvamarin.clientappfriends.domain.enums.Psychotype;
import com.akvamarin.clientappfriends.domain.enums.Sex;
import com.akvamarin.clientappfriends.domain.enums.Smoking;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewUserDTO implements Serializable {

    private Long id;

    private String email;

    private String username;

    private String phone;

    private String dateOfBirthday;

    private String nickname;

    private Sex sex;

    private String aboutMe;

    private Smoking smoking;

    private Alcohol alcohol;

    private Psychotype psychotype;

    private String urlAvatar;

    private CityDTO cityDTO;

    private Set<String> roles;

    private String vkId;

}
