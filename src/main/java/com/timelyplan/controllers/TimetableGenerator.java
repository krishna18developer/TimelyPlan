package com.timelyplan.controllers;

import com.timelyplan.models.*;
import java.util.*;
import java.time.LocalTime;

public class TimetableGenerator {
    private List<Subject> subjects;
    private List<Instructor> instructors;
    private Map<String, Boolean> rooms;  // room -> isLab
    private List<TimeSlot> timeSlots;
    private Map<String, String> classToRoom;  // classId -> permanentRoom
    private boolean allowConsecutiveClasses;
    private int maxConsecutiveLabHours;
    private Map<String, List<TimetableEntry>> timetable; // Day -> List of entries
    private List<CourseClass> classes; // Added field for classes

    public TimetableGenerator() {
        subjects = new ArrayList<>();
        instructors = new ArrayList<>();
        rooms = new HashMap<>();
        timeSlots = new ArrayList<>();
        classToRoom = new HashMap<>();
        timetable = new HashMap<>();
        allowConsecutiveClasses = true;
        maxConsecutiveLabHours = 2;
        classes = new ArrayList<>(); // Initialize classes list
    }

    public void setMaxConsecutiveLabHours(int hours) {
        this.maxConsecutiveLabHours = hours;
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

    public void addRoom(String room, boolean isLab) {
        rooms.put(room, isLab);
    }

    public void setClassroomForClass(String classId, String room) {
        classToRoom.put(classId, room);
    }

    public void setTimeSlots(List<TimeSlot> slots) {
        this.timeSlots = new ArrayList<>(slots);
    }

    public void addCourseClass(CourseClass courseClass) {
        classes.add(courseClass);
        if (courseClass.getPermanentRoom() != null) {
            classToRoom.put(courseClass.getId(), courseClass.getPermanentRoom());
        }
    }

    private String getAppropriateRoom(Subject subject, String classId) {
        if (subject.isLab()) {
            // For lab subjects, use the assigned lab room from the class
            for (CourseClass courseClass : classes) {
                if (courseClass.getId().equals(classId)) {
                    String assignedLabRoom = courseClass.getLabRoomForSubject(subject.getId());
                    if (assignedLabRoom != null) {
                        return assignedLabRoom;
                    }
                    break;
                }
            }
            // If no lab room is assigned or class not found, find any available lab room
            return rooms.entrySet().stream()
                .filter(entry -> entry.getValue() && entry.getKey().startsWith("L"))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
        } else {
            // For regular subjects, use the permanent classroom
            return classToRoom.get(classId);
        }
    }

    private boolean isInstructorAvailable(Instructor instructor, String day, TimeSlot slot) {
        List<TimetableEntry> dayEntries = timetable.get(day);
        if (dayEntries == null) return true;
        
        for (TimetableEntry entry : dayEntries) {
            if (entry.getTimeSlot().equals(slot) && entry.getInstructor().equals(instructor)) {
                return false;
            }
        }
        return true;
    }

    private boolean isRoomAvailable(String room, String day, TimeSlot slot) {
        List<TimetableEntry> dayEntries = timetable.get(day);
        if (dayEntries == null) return true;
        
        for (TimetableEntry entry : dayEntries) {
            if (entry.getTimeSlot().equals(slot) && entry.getRoom().equals(room)) {
                return false;
            }
        }
        return true;
    }

    public Map<String, List<TimetableEntry>> generateTimetable() {
        // Reset the timetable
        timetable.clear();
        List<String> days = Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY");
        days.forEach(day -> timetable.put(day, new ArrayList<>()));
        
        // Reset instructors' weekly hours
        instructors.forEach(Instructor::resetWeeklyHours);

        // Sort subjects by weekly hours (descending) to schedule more demanding subjects first
        subjects.sort((a, b) -> {
            if (a.isLab() && !b.isLab()) return -1; // Schedule labs first
            if (!a.isLab() && b.isLab()) return 1;
            return Integer.compare(b.getWeeklyHours(), a.getWeeklyHours());
        });

        for (Subject subject : subjects) {
            // Find all classes that have this subject
            List<CourseClass> classesWithSubject = new ArrayList<>();
            for (CourseClass courseClass : classes) {
                if (courseClass.getSubjectToInstructorMap().containsKey(subject.getId())) {
                    classesWithSubject.add(courseClass);
                }
            }
                
            if (classesWithSubject.isEmpty()) {
                continue; // Skip if no class has this subject
            }

            for (CourseClass courseClass : classesWithSubject) {
                String instructorId = courseClass.getInstructorForSubject(subject.getId());
                if (instructorId == null) continue;

                Instructor instructor = null;
                for (Instructor inst : instructors) {
                    if (inst.getId().equals(instructorId)) {
                        instructor = inst;
                        break;
                    }
                }

                if (instructor == null || !instructor.canTeachMore(subject.getId())) {
                    continue;
                }

                // Get appropriate room
                String room;
                if (subject.isLab()) {
                    room = courseClass.getLabRoomForSubject(subject.getId());
                    if (room == null) {
                        // If no specific lab room assigned, find any available lab room
                        room = findAvailableLabRoom();
                    }
                } else {
                    room = courseClass.getPermanentRoom();
                }

                if (room == null) {
                    continue;
                }

                // Schedule all hours for this subject
                int hoursToSchedule = subject.getWeeklyHours();
                int scheduledHours = 0;

                while (scheduledHours < hoursToSchedule) {
                    boolean scheduled = false;
                    
                    // Try to schedule in each day and time slot
                    dayLoop: for (String day : days) {
                        for (TimeSlot slot : timeSlots) {
                            if (slot.isBreak() || slot.getDurationMinutes() < subject.getDuration()) {
                                continue;
                            }

                            if (isInstructorAvailable(instructor, day, slot) && isRoomAvailable(room, day, slot)) {
                                TimetableEntry entry = new TimetableEntry(subject, instructor, slot, room, day);
                                timetable.get(day).add(entry);
                                instructor.incrementHours(subject.getId());
                                scheduledHours++;
                                scheduled = true;
                                break dayLoop;
                            }
                        }
                    }
                    
                    if (!scheduled) {
                        throw new RuntimeException("Unable to schedule all classes for " + subject.getName() +
                                                ". Check instructor availability and room assignments.");
                    }
                }
            }
        }

        // Sort entries in each day by time slot
        for (List<TimetableEntry> entries : timetable.values()) {
            entries.sort((a, b) -> a.getTimeSlot().getStartTime().compareTo(b.getTimeSlot().getStartTime()));
        }

        return timetable;
    }

    private String findAvailableLabRoom() {
        for (Map.Entry<String, Boolean> entry : rooms.entrySet()) {
            if (entry.getValue() && entry.getKey().startsWith("L")) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Map<String, List<TimetableEntry>> getTimetable() {
        return new HashMap<>(timetable);
    }
} 