# TimelyPlan - College Timetable Generator

TimelyPlan is a Java Swing-based application for generating and managing college timetables. It provides an intuitive interface for configuring instructors, subjects, time slots, and rooms, then automatically generates optimized weekly timetables.

## Features

- **Instructor Management**
  - Add instructors with their maximum weekly working hours
  - Track and manage instructor schedules

- **Subject & Lab Management**
  - Define subjects with required weekly hours
  - Support for both theory classes and lab sessions
  - Configurable duration for different types of classes

- **Flexible Time Slot Configuration**
  - Set custom time slots for classes
  - Define breaks and lunch periods
  - Support for different session durations

- **Room Management**
  - Add and manage multiple classrooms and labs
  - Automatic room assignment based on class requirements

- **Automatic Timetable Generation**
  - Generate conflict-free timetables
  - Fair distribution of subjects across the week
  - Option to allow/disallow consecutive classes for instructors

- **Export Capabilities**
  - View timetable in a clear, tabular format
  - Export timetable to PDF
  - Print functionality

## Requirements

- Java 11 or higher
- Maven for dependency management

## Building the Application

1. Clone the repository:
   ```bash
   git clone [repository-url]
   cd TimelyPlan
   ```

2. Build with Maven:
   ```bash
   mvn clean package
   ```

3. Run the application:
   ```bash
   java -jar target/timelyplan-1.0-SNAPSHOT.jar
   ```

## Using the Application

1. **Add Instructors**
   - Go to the "Instructors" tab
   - Enter instructor name and maximum weekly hours
   - Click "Add Instructor"

2. **Define Subjects**
   - Navigate to the "Subjects" tab
   - Enter subject details (name, weekly hours, duration)
   - Specify if it's a lab session
   - Click "Add Subject"

3. **Configure Time Slots**
   - Go to the "Time Slots" tab
   - Set start and end times
   - Select slot type (Regular/Break/Lunch)
   - Click "Add Time Slot"

4. **Add Rooms**
   - Use the "Rooms" tab
   - Enter room names/numbers
   - Click "Add Room"

5. **Generate Timetable**
   - Go to the "Generate" tab
   - Choose whether to allow consecutive classes
   - Click "Generate Timetable"
   - Use "Export to PDF" to save or print the timetable

## Contributing

Contributions are welcome! Please feel free to submit pull requests.

## License

This project is licensed under the MIT License - see the LICENSE file for details. 