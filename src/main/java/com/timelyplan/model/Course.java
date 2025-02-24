package com.timelyplan.model;

public class Course {
    private String courseId;
    private String courseName;
    private int weeklyHours;
    private boolean requiresLab;

    public Course(String courseId, String courseName, int weeklyHours, boolean requiresLab) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.weeklyHours = weeklyHours;
        this.requiresLab = requiresLab;
    }

    // Getters and Setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(int weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public boolean isRequiresLab() {
        return requiresLab;
    }

    public void setRequiresLab(boolean requiresLab) {
        this.requiresLab = requiresLab;
    }

    @Override
    public String toString() {
        return courseName + " (" + courseId + ")";
    }
} 