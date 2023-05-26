package com.akvamarin.clientappfriends.domain.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityDTO implements Serializable, Parcelable {

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


    @NonNull
    @Override
    public String toString() {
        return name;
    }

    protected CityDTO(Parcel in) {
        id = in.readLong();
        name = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
        federalDistrictID = in.readLong();
        federalDistrictName = in.readString();
        regionID = in.readLong();
        regionName = in.readString();
        countryID = in.readLong();
        countryName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeLong(federalDistrictID);
        dest.writeString(federalDistrictName);
        dest.writeLong(regionID);
        dest.writeString(regionName);
        dest.writeLong(countryID);
        dest.writeString(countryName);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CityDTO> CREATOR = new Creator<>() {
        @Override
        public CityDTO createFromParcel(Parcel in) {
            return new CityDTO(in);
        }

        @Override
        public CityDTO[] newArray(int size) {
            return new CityDTO[size];
        }
    };
}
