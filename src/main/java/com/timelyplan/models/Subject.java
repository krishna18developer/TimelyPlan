package com.timelyplan.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Subject {
    private String id;
    private String name;
    private int weeklyHours;
    private boolean isLab;
    private int duration; // Duration in minutes
    private String requiredRoomType; // "CLASSROOM" or "LAB"

    // No-args constructor for Jackson
    public Subject() {}

    @JsonCreator
    public Subject(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("weeklyHours") int weeklyHours,
        @JsonProperty("isLab") boolean isLab,
        @JsonProperty("duration") int duration) {
        this.id = id;
        this.name = name;
        this.weeklyHours = weeklyHours;
        this.isLab = isLab;
        this.duration = duration;
        this.requiredRoomType = isLab ? "LAB" : "CLASSROOM";
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("weeklyHours")
    public int getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(int weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    @JsonProperty("isLab")
    public boolean isLab() {
        return isLab;
    }

    @JsonProperty("duration")
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getRequiredRoomType() {
        return requiredRoomType;
    }

    @Override
    public String toString() {
        return name + (isLab ? " (Lab)" : "");
    }
} 