package com.akvamarin.clientappfriends.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityDTO implements Serializable {

    private Long id;

    private String name;

    private Double lat;

    private Double lon;

    private Long federalDistrictID;

    private String federalDistrictName;

    private Long regionID;

    private String regionName;

    private Long countryID;

    private String countryName;

}
