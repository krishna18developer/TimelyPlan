package com.timelyplan.utils;

import com.timelyplan.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DataManager {
    private static final Logger LOGGER = Logger.getLogger(DataManager.class.getName());
    private static final String DATA_DIR = "data";
    private static final String INSTRUCTORS_FILE = "instructors.json";
    private static final String SUBJECTS_FILE = "subjects.json";
    private static final String TIMESLOTS_FILE = "timeslots.json";
    private static final String ROOMS_FILE = "rooms.json";
    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());
        
        try {
            // Create data directory if it doesn't exist
            File dataDir = new File(DATA_DIR);
            if (!dataDir.exists() && !dataDir.mkdirs()) {
                LOGGER.severe("Failed to create data directory");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating data directory", e);
        }
    }

    public static void saveInstructors(List<Instructor> instructors) {
        try {
            saveToFile(new File(DATA_DIR, INSTRUCTORS_FILE), instructors);
            LOGGER.info("Successfully saved " + instructors.size() + " instructors");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving instructors", e);
        }
    }

    public static List<Instructor> loadInstructors() {
        try {
            List<Instructor> instructors = loadFromFile(new File(DATA_DIR, INSTRUCTORS_FILE), 
                mapper.getTypeFactory().constructCollectionType(List.class, Instructor.class));
            LOGGER.info("Successfully loaded " + instructors.size() + " instructors");
            return instructors;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading instructors", e);
            return new ArrayList<>();
        }
    }

    public static void saveSubjects(List<Subject> subjects) {
        try {
            saveToFile(new File(DATA_DIR, SUBJECTS_FILE), subjects);
            LOGGER.info("Successfully saved " + subjects.size() + " subjects");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving subjects", e);
        }
    }

    public static List<Subject> loadSubjects() {
        try {
            List<Subject> subjects = loadFromFile(new File(DATA_DIR, SUBJECTS_FILE),
                mapper.getTypeFactory().constructCollectionType(List.class, Subject.class));
            LOGGER.info("Successfully loaded " + subjects.size() + " subjects");
            return subjects;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading subjects", e);
            return new ArrayList<>();
        }
    }

    public static void saveTimeSlots(List<TimeSlot> timeSlots) {
        try {
            saveToFile(new File(DATA_DIR, TIMESLOTS_FILE), timeSlots);
            LOGGER.info("Successfully saved " + timeSlots.size() + " time slots");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving time slots", e);
        }
    }

    public static List<TimeSlot> loadTimeSlots() {
        try {
            List<TimeSlot> timeSlots = loadFromFile(new File(DATA_DIR, TIMESLOTS_FILE),
                mapper.getTypeFactory().constructCollectionType(List.class, TimeSlot.class));
            LOGGER.info("Successfully loaded " + timeSlots.size() + " time slots");
            return timeSlots;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading time slots", e);
            return new ArrayList<>();
        }
    }

    public static void saveRooms(List<String> rooms) {
        try {
            saveToFile(new File(DATA_DIR, ROOMS_FILE), rooms);
            LOGGER.info("Successfully saved " + rooms.size() + " rooms");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving rooms", e);
        }
    }

    public static List<String> loadRooms() {
        try {
            List<String> rooms = loadFromFile(new File(DATA_DIR, ROOMS_FILE),
                mapper.getTypeFactory().constructCollectionType(List.class, String.class));
            LOGGER.info("Successfully loaded " + rooms.size() + " rooms");
            return rooms;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading rooms", e);
            return new ArrayList<>();
        }
    }

    private static <T> void saveToFile(File file, T data) throws IOException {
        File parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("Failed to create directory: " + parent);
        }
        mapper.writeValue(file, data);
    }

    private static <T> List<T> loadFromFile(File file, 
            com.fasterxml.jackson.databind.JavaType type) throws IOException {
        if (file.exists()) {
            return mapper.readValue(file, type);
        }
        return new ArrayList<>();
    }
} 