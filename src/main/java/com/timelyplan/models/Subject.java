package com.timelyplan.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

public class Subject {
    private String id;
    private String name;
    private int weeklyHours;
    private boolean isLab;
    private int duration; // Duration in minutes
    private String requiredRoomType; // "CLASSROOM" or "LAB"
    private Set<String> eligibleInstructors; // Set of Instructor IDs who can teach this subject

    // No-args constructor for Jackson
    public Subject() {
        this.eligibleInstructors = new HashSet<>();
    }

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
        this.eligibleInstructors = new HashSet<>();
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

    @JsonProperty("eligibleInstructors")
    public Set<String> getEligibleInstructors() {
        return new HashSet<>(eligibleInstructors);
    }

    @JsonProperty("eligibleInstructors")
    public void setEligibleInstructors(Set<String> instructors) {
        this.eligibleInstructors = new HashSet<>(instructors);
    }

    public void addEligibleInstructor(String instructorId) {
        this.eligibleInstructors.add(instructorId);
    }

    public void removeEligibleInstructor(String instructorId) {
        this.eligibleInstructors.remove(instructorId);
    }

    public boolean isInstructorEligible(String instructorId) {
        return this.eligibleInstructors.contains(instructorId);
    }

    @Override
    public String toString() {
        return name + (isLab ? " (Lab)" : "");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Subject)) return false;
        Subject other = (Subject) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
} 