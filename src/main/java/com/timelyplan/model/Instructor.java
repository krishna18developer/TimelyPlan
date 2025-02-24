package com.timelyplan.model;

import java.util.ArrayList;
import java.util.List;

public class Instructor {
    private String instructorId;
    private String name;
    private List<Course> courses;
    private List<TimeSlot> availableSlots;
    private int maxWeeklyHours;

    public Instructor(String instructorId, String name, int maxWeeklyHours) {
        this.instructorId = instructorId;
        this.name = name;
        this.maxWeeklyHours = maxWeeklyHours;
        this.courses = new ArrayList<>();
        this.availableSlots = new ArrayList<>();
    }

    // Getters and Setters
    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public void addCourse(Course course) {
        this.courses.add(course);
    }

    public List<TimeSlot> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<TimeSlot> availableSlots) {
        this.availableSlots = availableSlots;
    }

    public void addAvailableSlot(TimeSlot slot) {
        this.availableSlots.add(slot);
    }

    public int getMaxWeeklyHours() {
        return maxWeeklyHours;
    }

    public void setMaxWeeklyHours(int maxWeeklyHours) {
        this.maxWeeklyHours = maxWeeklyHours;
    }

    @Override
    public String toString() {
        return name + " (" + instructorId + ")";
    }
} 