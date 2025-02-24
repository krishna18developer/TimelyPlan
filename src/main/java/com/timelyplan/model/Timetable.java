package com.timelyplan.model;

import java.util.HashMap;
import java.util.Map;

public class Timetable {
    private Section section;
    private Map<TimeSlot, TimetableEntry> schedule;

    public Timetable(Section section) {
        this.section = section;
        this.schedule = new HashMap<>();
    }

    public void addEntry(TimeSlot slot, Course course, Instructor instructor) {
        schedule.put(slot, new TimetableEntry(course, instructor));
    }

    public TimetableEntry getEntry(TimeSlot slot) {
        return schedule.get(slot);
    }

    public boolean isSlotAvailable(TimeSlot slot) {
        return !schedule.containsKey(slot);
    }

    public Section getSection() {
        return section;
    }

    public Map<TimeSlot, TimetableEntry> getSchedule() {
        return schedule;
    }

    public static class TimetableEntry {
        private Course course;
        private Instructor instructor;

        public TimetableEntry(Course course, Instructor instructor) {
            this.course = course;
            this.instructor = instructor;
        }

        public Course getCourse() {
            return course;
        }

        public Instructor getInstructor() {
            return instructor;
        }

        @Override
        public String toString() {
            return course.getCourseName() + "\n" + instructor.getName();
        }
    }
} 