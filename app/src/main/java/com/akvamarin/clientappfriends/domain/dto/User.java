package com.akvamarin.clientappfriends.domain.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {
    @SerializedName("id")
    private long id;

    @SerializedName("nickname")
    private String name;

    @SerializedName("dateOfBirthday")
    private String dateOfBirthday;

    @SerializedName("city")
    private String city;

    @SerializedName("country")
    private String country;

    @SerializedName("urlAvatar")
    private String urlAvatar; /* TODO: картинка на аву */

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("sex")
    private String sex;

    private int age; //-----------------------------------------------delete

    public User(String name, int age, String urlAvatar) {
        this.name = name;
        this.urlAvatar = urlAvatar;
        this.country = "Россия";
        this.city = "Иркутск";
        this.age = age;
    }

    public User() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDateOfBirthday() {
        return dateOfBirthday;
    }

    public void setDateOfBirthday(String dateOfBirthday) {
        this.dateOfBirthday = dateOfBirthday;
    }

    public int getAge() {
        return age;
    }

}
