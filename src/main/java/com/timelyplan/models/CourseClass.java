package com.timelyplan.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

public class CourseClass {
    private String id;
    private String name;
    private int semester;
    private Map<String, String> subjectToInstructorMap; // Subject ID -> Instructor ID
    private Map<String, String> subjectToLabRoomMap; // Subject ID -> Lab Room (for lab subjects)
    private String permanentRoom;  // Regular classroom for non-lab subjects

    // No-args constructor for Jackson
    public CourseClass() {
        this.subjectToInstructorMap = new HashMap<>();
        this.subjectToLabRoomMap = new HashMap<>();
    }

    @JsonCreator
    public CourseClass(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("semester") int semester) {
        this.id = id;
        this.name = name;
        this.semester = semester;
        this.subjectToInstructorMap = new HashMap<>();
        this.subjectToLabRoomMap = new HashMap<>();
        this.permanentRoom = null;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("semester")
    public int getSemester() {
        return semester;
    }

    @JsonProperty("subjectToInstructorMap")
    public Map<String, String> getSubjectToInstructorMap() {
        return new HashMap<>(subjectToInstructorMap);
    }

    @JsonProperty("subjectToInstructorMap")
    public void setSubjectToInstructorMap(Map<String, String> map) {
        this.subjectToInstructorMap = new HashMap<>(map);
    }

    @JsonProperty("subjectToLabRoomMap")
    public Map<String, String> getSubjectToLabRoomMap() {
        return new HashMap<>(subjectToLabRoomMap);
    }

    @JsonProperty("subjectToLabRoomMap")
    public void setSubjectToLabRoomMap(Map<String, String> map) {
        this.subjectToLabRoomMap = new HashMap<>(map);
    }

    @JsonProperty("permanentRoom")
    public String getPermanentRoom() {
        return permanentRoom;
    }

    @JsonProperty("permanentRoom")
    public void setPermanentRoom(String permanentRoom) {
        this.permanentRoom = permanentRoom;
    }

    public void assignInstructorToSubject(String subjectId, String instructorId) {
        subjectToInstructorMap.put(subjectId, instructorId);
    }

    public String getInstructorForSubject(String subjectId) {
        return subjectToInstructorMap.get(subjectId);
    }

    public void assignLabRoomToSubject(String subjectId, String labRoom) {
        subjectToLabRoomMap.put(subjectId, labRoom);
    }

    public String getLabRoomForSubject(String subjectId) {
        return subjectToLabRoomMap.get(subjectId);
    }

    public void removeSubject(String subjectId) {
        subjectToInstructorMap.remove(subjectId);
        subjectToLabRoomMap.remove(subjectId);
    }

    @Override
    public String toString() {
        return name + " (Semester " + semester + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CourseClass)) return false;
        CourseClass other = (CourseClass) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
} 