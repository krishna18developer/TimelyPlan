package com.timelyplan.model;

import java.util.ArrayList;
import java.util.List;

public class Section {
    private String sectionId;
    private String name;
    private int year;
    private List<Course> courses;
    private List<TimeSlot> preferredFreeSlots;
    private boolean hasHalfDaySaturday;

    public Section(String sectionId, String name, int year, boolean hasHalfDaySaturday) {
        this.sectionId = sectionId;
        this.name = name;
        this.year = year;
        this.hasHalfDaySaturday = hasHalfDaySaturday;
        this.courses = new ArrayList<>();
        this.preferredFreeSlots = new ArrayList<>();
    }

    // Getters and Setters
    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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

    public List<TimeSlot> getPreferredFreeSlots() {
        return preferredFreeSlots;
    }

    public void setPreferredFreeSlots(List<TimeSlot> preferredFreeSlots) {
        this.preferredFreeSlots = preferredFreeSlots;
    }

    public void addPreferredFreeSlot(TimeSlot slot) {
        this.preferredFreeSlots.add(slot);
    }

    public boolean isHasHalfDaySaturday() {
        return hasHalfDaySaturday;
    }

    public void setHasHalfDaySaturday(boolean hasHalfDaySaturday) {
        this.hasHalfDaySaturday = hasHalfDaySaturday;
    }

    @Override
    public String toString() {
        return name + " (Year " + year + ")";
    }
} 