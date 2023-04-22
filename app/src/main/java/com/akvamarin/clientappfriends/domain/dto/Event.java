package com.akvamarin.clientappfriends.domain.dto;

import androidx.annotation.NonNull;

import com.akvamarin.clientappfriends.domain.enums.Partner;
import com.akvamarin.clientappfriends.domain.enums.DayPeriodOfTime;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
public class Event implements Serializable {
    private User user;
    private String category;
    private String eventName;
    private String eventDescription;
    private LocalDate date;
    private Partner partner;
    private DayPeriodOfTime periodOfTime;
    private LocalDateTime dateTimeCreated;

    public Event() {}

    public Event(User user, String category, String eventName, String eventDescription, LocalDate date, Partner partner, LocalDateTime dateTimeCreated) {
        this.user = user;
        this.category = category;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.date = date;
        this.partner = partner;
        this.dateTimeCreated = dateTimeCreated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public DayPeriodOfTime getPeriodOfTime() {
        return periodOfTime;
    }

    public void setPeriodOfTime(DayPeriodOfTime periodOfTime) {
        this.periodOfTime = periodOfTime;
    }

    public LocalDateTime getDateTimeCreated() {
        return dateTimeCreated;
    }

    public void setDateTimeCreated(LocalDateTime dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    @Override
    @NonNull
    public String toString() {
        return "Event{" +
                "user='" + user + '\'' +
                ", category='" + category + '\'' +
                ", eventName='" + eventName + '\'' +
                ", eventDescription='" + eventDescription + '\'' +
                ", date=" + date +
                ", partner='" + partner + '\'' +
                ", periodOfTime='" + periodOfTime + '\'' +
                '}';
    }
}
