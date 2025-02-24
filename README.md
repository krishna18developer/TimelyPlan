 # TimelyPlan - Timetable Management System

TimelyPlan is a Java-based application designed to help educational institutions create and manage timetables for courses, instructors, and sections. It provides an intuitive interface for managing scheduling constraints and generating conflict-free timetables.

## Features

- Course Management
  - Add and manage courses with weekly hour requirements
  - Specify lab requirements for courses

- Instructor Management
  - Define instructor details and availability
  - Set maximum weekly teaching hours
  - Assign courses to instructors

- Section Management
  - Create sections with year-based classification
  - Configure half-day schedules for Saturdays
  - Set preferred free periods

- Timetable Generation
  - Automatically generate conflict-free timetables
  - Consider instructor availability and course requirements
  - Support for custom constraints

- Export Options
  - Export timetables to Excel format
  - Export timetables to PDF format

## System Requirements

- Java Development Kit (JDK) 21 or later
- Maven 3.6 or later

## Building the Application

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/timelyplan.git
   cd timelyplan
   ```

2. Build with Maven:
   ```bash
   mvn clean package
   ```

## Running the Application

After building, run the application using:

```bash
java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -jar target/timelyplan-1.0-SNAPSHOT.jar
```

## Usage Guide

1. **Adding Courses**
   - Navigate to the Courses tab
   - Fill in the course details (ID, Name, Weekly Hours)
   - Check "Requires Lab" if applicable
   - Click "Add Course"

2. **Managing Instructors**
   - Go to the Instructors tab
   - Enter instructor information
   - Set maximum weekly hours
   - Click "Add Instructor"

3. **Creating Sections**
   - Switch to the Sections tab
   - Provide section details
   - Select year and half-day preference
   - Click "Add Section"

4. **Generating Timetables**
   - Open the Timetable tab
   - Select a section from the dropdown
   - Click "Generate Timetable"
   - Export to Excel or PDF using the File menu