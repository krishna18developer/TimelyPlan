package com.timelyplan.models;

public class TimetableEntry {
    private Subject subject;
    private Instructor instructor;
    private TimeSlot timeSlot;
    private String room;
    private String day; // "MONDAY", "TUESDAY", etc.

    public TimetableEntry(Subject subject, Instructor instructor, TimeSlot timeSlot, String room, String day) {
        this.subject = subject;
        this.instructor = instructor;
        this.timeSlot = timeSlot;
        this.room = room;
        this.day = day;
    }

    public Subject getSubject() {
        return subject;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public String getRoom() {
        return room;
    }

    public String getDay() {
        return day;
    }

    public boolean conflicts(TimetableEntry other) {
        if (!this.day.equals(other.day)) {
            return false;
        }
        
        if (this.instructor.equals(other.instructor)) {
            return this.timeSlot.overlaps(other.timeSlot);
        }
        
        if (this.room.equals(other.room)) {
            return this.timeSlot.overlaps(other.timeSlot);
        }
        
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s\n%s\n%s\nRoom: %s",
                subject.getName(),
                instructor.getName(),
                timeSlot.toString(),
                room);
    }
} 