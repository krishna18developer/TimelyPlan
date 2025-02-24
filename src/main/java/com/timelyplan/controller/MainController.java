package com.timelyplan.controller;

import com.timelyplan.model.*;
import com.timelyplan.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    // Course Tab
    @FXML private TextField courseIdField;
    @FXML private TextField courseNameField;
    @FXML private TextField weeklyHoursField;
    @FXML private CheckBox requiresLabCheckBox;
    @FXML private TableView<Course> coursesTable;
    @FXML private TableColumn<Course, String> courseIdColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, Integer> weeklyHoursColumn;
    @FXML private TableColumn<Course, Boolean> requiresLabColumn;

    // Instructor Tab
    @FXML private TextField instructorIdField;
    @FXML private TextField instructorNameField;
    @FXML private TextField maxWeeklyHoursField;
    @FXML private TableView<Instructor> instructorsTable;
    @FXML private TableColumn<Instructor, String> instructorIdColumn;
    @FXML private TableColumn<Instructor, String> instructorNameColumn;
    @FXML private TableColumn<Instructor, Integer> maxWeeklyHoursColumn;

    // Section Tab
    @FXML private TextField sectionIdField;
    @FXML private TextField sectionNameField;
    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private CheckBox halfDaySaturdayCheckBox;
    @FXML private TableView<Section> sectionsTable;
    @FXML private TableColumn<Section, String> sectionIdColumn;
    @FXML private TableColumn<Section, String> sectionNameColumn;
    @FXML private TableColumn<Section, Integer> yearColumn;
    @FXML private TableColumn<Section, Boolean> halfDayColumn;

    // Timetable Tab
    @FXML private ComboBox<Section> sectionComboBox;
    @FXML private TableView<List<String>> timetableTable;

    private ObservableList<Course> courses = FXCollections.observableArrayList();
    private ObservableList<Instructor> instructors = FXCollections.observableArrayList();
    private ObservableList<Section> sections = FXCollections.observableArrayList();
    private Timetable currentTimetable;

    @FXML
    public void initialize() {
        // Initialize Course Table
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        weeklyHoursColumn.setCellValueFactory(new PropertyValueFactory<>("weeklyHours"));
        requiresLabColumn.setCellValueFactory(new PropertyValueFactory<>("requiresLab"));
        coursesTable.setItems(courses);

        // Initialize Instructor Table
        instructorIdColumn.setCellValueFactory(new PropertyValueFactory<>("instructorId"));
        instructorNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        maxWeeklyHoursColumn.setCellValueFactory(new PropertyValueFactory<>("maxWeeklyHours"));
        instructorsTable.setItems(instructors);

        // Initialize Section Table
        sectionIdColumn.setCellValueFactory(new PropertyValueFactory<>("sectionId"));
        sectionNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        halfDayColumn.setCellValueFactory(new PropertyValueFactory<>("hasHalfDaySaturday"));
        sectionsTable.setItems(sections);

        // Initialize Year ComboBox
        yearComboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4));

        // Initialize Section ComboBox
        sectionComboBox.setItems(sections);
    }

    @FXML
    private void addCourse() {
        try {
            String courseId = courseIdField.getText();
            String courseName = courseNameField.getText();
            int weeklyHours = Integer.parseInt(weeklyHoursField.getText());
            boolean requiresLab = requiresLabCheckBox.isSelected();

            Course course = new Course(courseId, courseName, weeklyHours, requiresLab);
            courses.add(course);

            // Clear fields
            courseIdField.clear();
            courseNameField.clear();
            weeklyHoursField.clear();
            requiresLabCheckBox.setSelected(false);
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for weekly hours.");
        }
    }

    @FXML
    private void addInstructor() {
        try {
            String instructorId = instructorIdField.getText();
            String name = instructorNameField.getText();
            int maxWeeklyHours = Integer.parseInt(maxWeeklyHoursField.getText());

            Instructor instructor = new Instructor(instructorId, name, maxWeeklyHours);
            instructors.add(instructor);

            // Clear fields
            instructorIdField.clear();
            instructorNameField.clear();
            maxWeeklyHoursField.clear();
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for max weekly hours.");
        }
    }

    @FXML
    private void addSection() {
        String sectionId = sectionIdField.getText();
        String name = sectionNameField.getText();
        Integer year = yearComboBox.getValue();
        boolean halfDay = halfDaySaturdayCheckBox.isSelected();

        if (year == null) {
            showError("Please select a year.");
            return;
        }

        Section section = new Section(sectionId, name, year, halfDay);
        sections.add(section);

        // Clear fields
        sectionIdField.clear();
        sectionNameField.clear();
        yearComboBox.setValue(null);
        halfDaySaturdayCheckBox.setSelected(false);
    }

    @FXML
    private void generateTimetable() {
        Section selectedSection = sectionComboBox.getValue();
        if (selectedSection == null) {
            showError("Please select a section.");
            return;
        }

        TimetableGenerator generator = new TimetableGenerator(selectedSection, new ArrayList<>(instructors));
        currentTimetable = generator.generate();
        displayTimetable();
    }

    private void displayTimetable() {
        // Clear existing columns
        timetableTable.getColumns().clear();

        // Add period column
        TableColumn<List<String>, String> periodColumn = new TableColumn<>("Period");
        periodColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        timetableTable.getColumns().add(periodColumn);

        // Add day columns
        for (int i = 0; i < TimetableGenerator.DAYS.length; i++) {
            final int dayIndex = i + 1;
            TableColumn<List<String>, String> dayColumn = new TableColumn<>(TimetableGenerator.DAYS[i]);
            dayColumn.setCellValueFactory(data -> 
                new javafx.beans.property.SimpleStringProperty(data.getValue().get(dayIndex)));
            timetableTable.getColumns().add(dayColumn);
        }

        // Populate data
        ObservableList<List<String>> data = FXCollections.observableArrayList();
        for (int period = 1; period <= 8; period++) {
            List<String> row = new ArrayList<>();
            row.add("Period " + period);
            
            for (String day : TimetableGenerator.DAYS) {
                TimeSlot slot = new TimeSlot(TimeSlot.DayOfWeek.valueOf(day), period);
                Timetable.TimetableEntry entry = currentTimetable.getEntry(slot);
                row.add(entry != null ? entry.toString() : "");
            }
            
            data.add(row);
        }

        timetableTable.setItems(data);
    }

    @FXML
    private void exportToExcel() {
        if (currentTimetable == null) {
            showError("Please generate a timetable first.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx"));
        File file = fileChooser.showSaveDialog(null);
        
        if (file != null) {
            try {
                TimetableExporter.exportToExcel(currentTimetable, file.getAbsolutePath());
                showInfo("Timetable exported successfully!");
            } catch (Exception e) {
                showError("Failed to export timetable: " + e.getMessage());
            }
        }
    }

    @FXML
    private void exportToPDF() {
        if (currentTimetable == null) {
            showError("Please generate a timetable first.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);
        
        if (file != null) {
            try {
                TimetableExporter.exportToPDF(currentTimetable, file.getAbsolutePath());
                showInfo("Timetable exported successfully!");
            } catch (Exception e) {
                showError("Failed to export timetable: " + e.getMessage());
            }
        }
    }

    @FXML
    private void exit() {
        Stage stage = (Stage) coursesTable.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 