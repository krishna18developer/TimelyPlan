package com.timelyplan.models;

import java.util.HashMap;
import java.util.Map;

public class Instructor {
    private String id;
    private String name;
    private int maxWeeklyHours;
    private Map<String, Integer> subjectHourLimits; // Subject ID -> Weekly hour limit
    private Map<String, Integer> currentWeeklyHours; // Subject ID -> Current assigned hours

    public Instructor(String id, String name, int maxWeeklyHours) {
        this.id = id;
        this.name = name;
        this.maxWeeklyHours = maxWeeklyHours;
        this.subjectHourLimits = new HashMap<>();
        this.currentWeeklyHours = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMaxWeeklyHours() {
        return maxWeeklyHours;
    }

    public void setMaxWeeklyHours(int maxWeeklyHours) {
        this.maxWeeklyHours = maxWeeklyHours;
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

    public Map<String, Integer> getSubjectHourLimits() {
        return new HashMap<>(subjectHourLimits);
    }

    public Map<String, Integer> getCurrentWeeklyHours() {
        return new HashMap<>(currentWeeklyHours);
    }
} 