package com.akvamarin.clientappfriends.vk.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class VKUser implements Parcelable {
    public long id;
    public String firstName;
    public String lastName;
    public String photo;
    public String dateOfBirth;
    public String city;
    public String cityTitle;
    public String country;
    public String countryTitle;
    public int sex;
    public String about;
    public boolean deactivated;


    public VKUser(long id, String firstName, String lastName, String photo, String dateOfBirth,
                  String city, String cityTitle, String country, String countryTitle, int sex,
                  String about, boolean deactivated) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
        this.dateOfBirth = dateOfBirth;
        this.city = city;
        this.cityTitle = cityTitle;
        this.country = country;
        this.countryTitle = countryTitle;
        this.sex = sex;
        this.about = about;
        this.deactivated = deactivated;
    }

    protected VKUser(Parcel in) {
        id = in.readLong();
        firstName = in.readString();
        lastName = in.readString();
        photo = in.readString();
        dateOfBirth = in.readString();
        city = in.readString();
        country = in.readString();
        sex = in.readInt();
        about = in.readString();
        deactivated = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(photo);
        dest.writeString(dateOfBirth);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeInt(sex);
        dest.writeString(about);
        dest.writeByte((byte) (deactivated ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VKUser> CREATOR = new Creator<>() {
        @Override
        public VKUser createFromParcel(Parcel in) {
            return new VKUser(in);
        }

        @Override
        public VKUser[] newArray(int size) {
            return new VKUser[size];
        }
    };

    public static VKUser parse(JSONObject json) {
        long id = json.optLong("id", 0);
        String firstName = json.optString("first_name", "");
        String lastName = json.optString("last_name", "");
        String photo = json.optString("photo_200", "");
        String dateOfBirth = json.optString("bdate", "");
        String city = json.optString("city", "");
        String country = json.optString("country", "");
        int sex = json.optInt("sex", 0);
        String about = json.optString("about", "");
        boolean deactivated = json.optBoolean("deactivated", false);

        String cityTitle = "";
        if (!city.isEmpty()) {
            JSONObject cityObject = json.optJSONObject("city");
            if (cityObject != null) {
                cityTitle = cityObject.optString("title", "");
            }
        }

        String countryTitle = "";
        if (!country.isEmpty()) {
            JSONObject countryObject = json.optJSONObject("country");
            if (countryObject != null) {
                countryTitle = countryObject.optString("title", "");
            }
        }

        return new VKUser(id, firstName, lastName, photo, dateOfBirth, city, cityTitle,
                country, countryTitle, sex, about, deactivated);
    }


}
