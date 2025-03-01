package com.timelyplan.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

public class Instructor {
    private String id;
    private String name;
    private int maxWeeklyHours;
    private Map<String, Integer> subjectHourLimits; // Subject ID -> Weekly hour limit
    private Map<String, Integer> currentWeeklyHours; // Subject ID -> Current assigned hours

    // No-args constructor for Jackson
    public Instructor() {
        this.subjectHourLimits = new HashMap<>();
        this.currentWeeklyHours = new HashMap<>();
    }

    @JsonCreator
    public Instructor(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("maxWeeklyHours") int maxWeeklyHours) {
        this.id = id;
        this.name = name;
        this.maxWeeklyHours = maxWeeklyHours;
        this.subjectHourLimits = new HashMap<>();
        this.currentWeeklyHours = new HashMap<>();
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("maxWeeklyHours")
    public int getMaxWeeklyHours() {
        return maxWeeklyHours;
    }

    public void setMaxWeeklyHours(int maxWeeklyHours) {
        this.maxWeeklyHours = maxWeeklyHours;
    }

    @JsonProperty("subjectHourLimits")
    public Map<String, Integer> getSubjectHourLimits() {
        return new HashMap<>(subjectHourLimits);
    }

    @JsonProperty("subjectHourLimits")
    public void setSubjectHourLimits(Map<String, Integer> limits) {
        this.subjectHourLimits = new HashMap<>(limits);
    }

    @JsonProperty("currentWeeklyHours")
    public Map<String, Integer> getCurrentWeeklyHours() {
        return new HashMap<>(currentWeeklyHours);
    }

    @JsonProperty("currentWeeklyHours")
    public void setCurrentWeeklyHours(Map<String, Integer> hours) {
        this.currentWeeklyHours = new HashMap<>(hours);
    }

    public void assignSubject(String subjectId, int weeklyHourLimit) {
        subjectHourLimits.put(subjectId, weeklyHourLimit);
        currentWeeklyHours.putIfAbsent(subjectId, 0);
    }

    public boolean canTeachMore(String subjectId) {
        int currentHours = currentWeeklyHours.getOrDefault(subjectId, 0);
        int limit = subjectHourLimits.getOrDefault(subjectId, 0);
        int totalCurrentHours = currentWeeklyHours.values().stream().mapToInt(Integer::intValue).sum();
        
        return currentHours < limit && totalCurrentHours < maxWeeklyHours;
    }

    public void incrementHours(String subjectId) {
        currentWeeklyHours.merge(subjectId, 1, Integer::sum);
    }

    public void resetWeeklyHours() {
        currentWeeklyHours.clear();
        subjectHourLimits.keySet().forEach(subject -> currentWeeklyHours.put(subject, 0));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Instructor)) return false;
        Instructor other = (Instructor) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
} 