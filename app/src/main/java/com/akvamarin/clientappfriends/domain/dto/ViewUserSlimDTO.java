package com.akvamarin.clientappfriends.domain.dto;

import java.io.Serializable;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewUserSlimDTO implements Serializable {

    private Long id;

    private String dateOfBirthday;

    private String nickname;

    private String urlAvatar;

    private CityDTO cityDTO;

    private Set<String> roles;

}
