package com.timelyplan.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

public class CourseClass {
    private String id;
    private String name;
    private int semester;
    private Map<String, String> subjectToInstructorMap; // Subject ID -> Instructor ID

    // No-args constructor for Jackson
    public CourseClass() {
        this.subjectToInstructorMap = new HashMap<>();
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

    public void assignInstructorToSubject(String subjectId, String instructorId) {
        subjectToInstructorMap.put(subjectId, instructorId);
    }

    public String getInstructorForSubject(String subjectId) {
        return subjectToInstructorMap.get(subjectId);
    }

    public void removeSubject(String subjectId) {
        subjectToInstructorMap.remove(subjectId);
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