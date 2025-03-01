package com.timelyplan.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;
import java.util.Objects;

public class TimeSlot {
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isBreak;
    private String type; // "REGULAR", "BREAK", "LUNCH"

    // No-args constructor for Jackson
    public TimeSlot() {}

    @JsonCreator
    public TimeSlot(
        @JsonProperty("startTime") LocalTime startTime,
        @JsonProperty("endTime") LocalTime endTime,
        @JsonProperty("isBreak") boolean isBreak,
        @JsonProperty("type") String type) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.isBreak = isBreak;
        this.type = type;
    }

    @JsonProperty("startTime")
    public LocalTime getStartTime() {
        return startTime;
    }

    @JsonProperty("endTime")
    public LocalTime getEndTime() {
        return endTime;
    }

    @JsonProperty("isBreak")
    public boolean isBreak() {
        return isBreak;
    }

    @JsonProperty("type")
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TimeSlot)) return false;
        TimeSlot other = (TimeSlot) obj;
        return startTime.equals(other.startTime) && 
               endTime.equals(other.endTime) && 
               type.equals(other.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime, type);
    }
} 