package com.akvamarin.clientappfriends.vk.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONObject;

public class VKUser implements Parcelable {
    public long id;
    public String firstName;
    public String lastName;
    public String photo;
    public String dateOfBirth;
    public String email;
    public boolean deactivated;


    public VKUser(long id, String firstName, String lastName, String photo, String dateOfBirth, String email, boolean deactivated) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.deactivated = deactivated;
    }

    protected VKUser(Parcel in) {
        id = in.readLong();
        firstName = in.readString();
        lastName = in.readString();
        photo = in.readString();
        dateOfBirth = in.readString();
        email = in.readString();
        deactivated = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(photo);
        dest.writeString(dateOfBirth);
        dest.writeString(email);
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
        String email = json.optString("email", "");      // email
        boolean deactivated = json.optBoolean("deactivated", false);
        return new VKUser(id, firstName, lastName, photo, dateOfBirth, email, deactivated);
    }


}
