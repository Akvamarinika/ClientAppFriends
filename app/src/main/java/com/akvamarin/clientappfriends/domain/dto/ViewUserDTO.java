package com.akvamarin.clientappfriends.domain.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.akvamarin.clientappfriends.domain.enums.Alcohol;
import com.akvamarin.clientappfriends.domain.enums.Psychotype;
import com.akvamarin.clientappfriends.domain.enums.Sex;
import com.akvamarin.clientappfriends.domain.enums.Smoking;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewUserDTO implements Serializable, Parcelable {

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

    protected ViewUserDTO(Parcel in) {
        id = in.readByte() == 0 ? null : in.readLong();
        email = in.readString();
        username = in.readString();
        phone = in.readString();
        dateOfBirthday = in.readString();
        nickname = in.readString();
        sex = Sex.valueOf(in.readString());
        aboutMe = in.readString();
        smoking = Smoking.valueOf(in.readString());
        alcohol = Alcohol.valueOf(in.readString());
        psychotype = Psychotype.valueOf(in.readString());
        urlAvatar = in.readString();
        cityDTO = in.readParcelable(CityDTO.class.getClassLoader());

        List<String> rolesList = new ArrayList<>();
        in.readList(rolesList, String.class.getClassLoader());
        roles = new HashSet<>(rolesList);

        vkId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(phone);
        dest.writeString(dateOfBirthday);
        dest.writeString(nickname);
        dest.writeString(sex.name());
        dest.writeString(aboutMe);
        dest.writeString(smoking.name());
        dest.writeString(alcohol.name());
        dest.writeString(psychotype.name());
        dest.writeString(urlAvatar);
        dest.writeParcelable((Parcelable) cityDTO, flags);

        List<String> rolesList = new ArrayList<>(roles);
        dest.writeList(rolesList);

        dest.writeString(vkId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ViewUserDTO> CREATOR = new Creator<>() {
        @Override
        public ViewUserDTO createFromParcel(Parcel in) {
            return new ViewUserDTO(in);
        }

        @Override
        public ViewUserDTO[] newArray(int size) {
            return new ViewUserDTO[size];
        }
    };
}
