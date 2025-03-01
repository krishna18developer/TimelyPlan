package com.timelyplan.controllers;

import com.timelyplan.models.*;
import java.util.*;
import java.time.LocalTime;

public class TimetableGenerator {
    private List<Subject> subjects;
    private List<Instructor> instructors;
    private List<String> rooms;
    private List<TimeSlot> timeSlots;
    private List<String> days;
    private boolean allowConsecutiveClasses;
    private Map<String, List<TimetableEntry>> timetable; // Day -> List of entries

    public TimetableGenerator() {
        this.subjects = new ArrayList<>();
        this.instructors = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.timeSlots = new ArrayList<>();
        this.days = Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY");
        this.timetable = new HashMap<>();
        this.allowConsecutiveClasses = true;
    }

    public void setAllowConsecutiveClasses(boolean allow) {
        this.allowConsecutiveClasses = allow;
    }

    public void addSubject(Subject subject) {
        subjects.add(subject);
    }

    public void addInstructor(Instructor instructor) {
        instructors.add(instructor);
    }

    public void addRoom(String room) {
        rooms.add(room);
    }

    public void setTimeSlots(List<TimeSlot> slots) {
        this.timeSlots = new ArrayList<>(slots);
    }

    public Map<String, List<TimetableEntry>> generateTimetable() {
        // Reset the timetable
        timetable.clear();
        days.forEach(day -> timetable.put(day, new ArrayList<>()));
        
        // Reset instructors' weekly hours
        instructors.forEach(Instructor::resetWeeklyHours);

        // Sort subjects by weekly hours (descending) to schedule more demanding subjects first
        subjects.sort((a, b) -> Integer.compare(b.getWeeklyHours(), a.getWeeklyHours()));

        for (Subject subject : subjects) {
            int remainingHours = subject.getWeeklyHours();
            while (remainingHours > 0) {
                boolean scheduled = false;
                
                // Find available instructor for this subject
                for (Instructor instructor : instructors) {
                    if (!instructor.canTeachMore(subject.getId())) {
                        continue;
                    }

                    // Try to schedule in each day and time slot
                    for (String day : days) {
                        for (TimeSlot slot : timeSlots) {
                            if (slot.isBreak()) {
                                continue;
                            }

                            if (slot.getDurationMinutes() < subject.getDuration()) {
                                continue;
                            }

                            // Find available room
                            for (String room : rooms) {
                                if (canSchedule(subject, instructor, slot, room, day)) {
                                    TimetableEntry entry = new TimetableEntry(subject, instructor, slot, room, day);
                                    timetable.get(day).add(entry);
                                    instructor.incrementHours(subject.getId());
                                    remainingHours--;
                                    scheduled = true;
                                    break;
                                }
                            }
                            if (scheduled) break;
                        }
                        if (scheduled) break;
                    }
                    if (scheduled) break;
                }
                
                if (!scheduled) {
                    throw new RuntimeException("Unable to schedule all classes for " + subject.getName());
                }
            }
        }

        // Sort entries in each day by time slot
        for (List<TimetableEntry> entries : timetable.values()) {
            entries.sort((a, b) -> a.getTimeSlot().getStartTime().compareTo(b.getTimeSlot().getStartTime()));
        }

        return timetable;
    }

    private boolean canSchedule(Subject subject, Instructor instructor, TimeSlot slot, String room, String day) {
        List<TimetableEntry> dayEntries = timetable.get(day);
        
        // Check for conflicts with existing entries
        for (TimetableEntry existing : dayEntries) {
            if (existing.getTimeSlot().overlaps(slot)) {
                if (existing.getInstructor().equals(instructor) || existing.getRoom().equals(room)) {
                    return false;
                }
            }
        }

        // Check for consecutive classes if not allowed
        if (!allowConsecutiveClasses) {
            for (TimetableEntry existing : dayEntries) {
                if (existing.getInstructor().equals(instructor)) {
                    LocalTime existingEnd = existing.getTimeSlot().getEndTime();
                    LocalTime existingStart = existing.getTimeSlot().getStartTime();
                    
                    if (slot.getStartTime().equals(existingEnd) || 
                        slot.getEndTime().equals(existingStart)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public Map<String, List<TimetableEntry>> getTimetable() {
        return new HashMap<>(timetable);
    }
} 