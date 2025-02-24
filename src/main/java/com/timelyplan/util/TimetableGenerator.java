package com.timelyplan.util;

import com.timelyplan.model.*;
import java.util.*;

public class TimetableGenerator {
    private static final int PERIODS_PER_DAY = 8;
    public static final String[] DAYS = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
    private final Section section;
    private final List<Instructor> instructors;
    private final Map<Course, Instructor> courseInstructorMap;
    private final Random random;

    public TimetableGenerator(Section section, List<Instructor> instructors) {
        this.section = section;
        this.instructors = instructors;
        this.courseInstructorMap = new HashMap<>();
        this.random = new Random();
        assignInstructorsToCourses();
    }

    private void assignInstructorsToCourses() {
        for (Course course : section.getCourses()) {
            for (Instructor instructor : instructors) {
                if (instructor.getCourses().contains(course)) {
                    courseInstructorMap.put(course, instructor);
                    break;
                }
            }
        }
    }

    public Timetable generate() {
        Timetable timetable = new Timetable(section);
        List<TimeSlot> allSlots = generateAllTimeSlots();
        
        // Remove preferred free slots
        allSlots.removeAll(section.getPreferredFreeSlots());
        
        // Remove Saturday afternoon slots if half day
        if (section.isHasHalfDaySaturday()) {
            allSlots.removeIf(slot -> 
                slot.getDay() == TimeSlot.DayOfWeek.SATURDAY && slot.getPeriod() > 4);
        }

        // Shuffle slots for random distribution
        Collections.shuffle(allSlots, random);

        // Assign courses to slots
        for (Course course : section.getCourses()) {
            Instructor instructor = courseInstructorMap.get(course);
            if (instructor == null) continue;

            int hoursNeeded = course.getWeeklyHours();
            int hoursAssigned = 0;

            for (TimeSlot slot : allSlots) {
                if (hoursAssigned >= hoursNeeded) break;
                
                if (canAssignSlot(timetable, slot, instructor)) {
                    timetable.addEntry(slot, course, instructor);
                    hoursAssigned++;
                }
            }
        }

        return timetable;
    }

    private List<TimeSlot> generateAllTimeSlots() {
        List<TimeSlot> slots = new ArrayList<>();
        for (TimeSlot.DayOfWeek day : TimeSlot.DayOfWeek.values()) {
            for (int period = 1; period <= PERIODS_PER_DAY; period++) {
                slots.add(new TimeSlot(day, period));
            }
        }
        return slots;
    }

    private boolean canAssignSlot(Timetable timetable, TimeSlot slot, Instructor instructor) {
        // Check if slot is already taken
        if (!timetable.isSlotAvailable(slot)) {
            return false;
        }

        // Check if instructor is available in this slot
        if (!instructor.getAvailableSlots().contains(slot)) {
            return false;
        }

        // Check if instructor has another class at the same time in different sections
        // This would require checking against other timetables, which we'll implement later

        return true;
    }
} 