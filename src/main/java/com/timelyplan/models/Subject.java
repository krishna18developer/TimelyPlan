package com.timelyplan.models;

public class Subject {
    private String id;
    private String name;
    private int weeklyHours;
    private boolean isLab;
    private int duration; // Duration in minutes
    private String requiredRoomType; // "CLASSROOM" or "LAB"

    public Subject(String id, String name, int weeklyHours, boolean isLab, int duration) {
        this.id = id;
        this.name = name;
        this.weeklyHours = weeklyHours;
        this.isLab = isLab;
        this.duration = duration;
        this.requiredRoomType = isLab ? "LAB" : "CLASSROOM";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(int weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public boolean isLab() {
        return isLab;
    }

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