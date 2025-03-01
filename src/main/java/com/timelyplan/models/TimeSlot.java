package com.timelyplan.models;

import java.time.LocalTime;

public class TimeSlot {
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isBreak;
    private String type; // "REGULAR", "BREAK", "LUNCH"

    public TimeSlot(LocalTime startTime, LocalTime endTime, boolean isBreak, String type) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.isBreak = isBreak;
        this.type = type;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean isBreak() {
        return isBreak;
    }

    public String getType() {
        return type;
    }

    public int getDurationMinutes() {
        return (int) java.time.Duration.between(startTime, endTime).toMinutes();
    }

    public boolean overlaps(TimeSlot other) {
        return !(this.endTime.isBefore(other.startTime) || this.startTime.isAfter(other.endTime));
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)",
                startTime.toString(),
                endTime.toString(),
                type);
    }
} 