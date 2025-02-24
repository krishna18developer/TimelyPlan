package com.timelyplan.model;

public class TimeSlot {
    private DayOfWeek day;
    private int period;
    private boolean isAvailable;

    public TimeSlot(DayOfWeek day, int period) {
        this.day = day;
        this.period = period;
        this.isAvailable = true;
    }

    public enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
    }

    // Getters and Setters
    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return day + " Period " + period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return period == timeSlot.period && day == timeSlot.day;
    }

    @Override
    public int hashCode() {
        return 31 * day.hashCode() + period;
    }
} 