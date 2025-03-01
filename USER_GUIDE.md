# TimelyPlan User Guide

This guide will help you effectively use the TimelyPlan application for generating and managing college timetables.

## Table of Contents
1. [Getting Started](#getting-started)
2. [Managing Instructors](#managing-instructors)
3. [Managing Subjects](#managing-subjects)
4. [Setting Up Time Slots](#setting-up-time-slots)
5. [Managing Rooms](#managing-rooms)
6. [Managing Classes](#managing-classes)
7. [Generating Timetables](#generating-timetables)
8. [Exporting Timetables](#exporting-timetables)

## Getting Started

1. Launch the TimelyPlan application
2. The interface consists of six main tabs:
   - Instructors
   - Subjects
   - Time Slots
   - Rooms
   - Classes
   - Generate

## Managing Instructors

### Adding an Instructor
1. Go to the "Instructors" tab
2. Enter the instructor's name in the "Name" field
3. Set their maximum weekly teaching hours using the spinner (1-40 hours)
4. Click "Add Instructor"

### Editing an Instructor
1. Select the instructor from the list
2. Click "Edit"
3. Update the name and/or maximum weekly hours
4. Confirm the changes

### Removing an Instructor
1. Select the instructor from the list
2. Click "Remove"

## Managing Subjects

### Adding a Subject
1. Navigate to the "Subjects" tab
2. Enter the subject name
3. Set weekly hours (1-20 hours)
4. Set duration (45-180 minutes)
5. Check "Is Lab Session" if applicable
6. Click "Add Subject"

### Important Subject Settings
- Weekly Hours: Total hours the subject should be taught per week
- Duration: Length of each session
- Lab Session: Indicates if the subject requires a lab room

## Setting Up Time Slots

### Adding Time Slots
1. Go to the "Time Slots" tab
2. Set the start time
3. Set the end time
4. Select the slot type:
   - REGULAR: Normal class period
   - BREAK: Short break between classes
   - LUNCH: Lunch break
5. Click "Add Time Slot"

### Time Slot Guidelines
- Ensure breaks between classes
- Account for lunch periods
- Consider appropriate duration for different types of classes

## Managing Rooms

### Adding Rooms
1. Navigate to the "Rooms" tab
2. Enter the room name/number
3. Click "Add Room"

### Room Management Tips
- Include both regular classrooms and labs
- Use clear, consistent naming conventions
- Consider room capacity and equipment needs

## Managing Classes

### Creating a New Class
1. Go to the "Classes" tab
2. Click "Add Class"
3. Enter the class name (e.g., "CSE-A")
4. Enter the semester number (1-8)
5. Click OK

### Assigning Subjects and Instructors
1. Select a class from the list
2. From "Available Subjects," select a subject to assign
3. Click ">>" to assign the subject
4. Select the assigned subject
5. Choose an instructor from the dropdown
6. Click "Assign Instructor"

### Editing Class Details
1. Select the class
2. Click "Edit Class"
3. Update name or semester
4. Confirm changes

### Removing Subjects
1. Select the class
2. Select the assigned subject
3. Click "<<" to remove

## Generating Timetables

### Generate a New Timetable
1. Go to the "Generate" tab
2. Choose whether to allow consecutive classes
3. Click "Generate Timetable"

### Timetable Generation Settings
- Allow Consecutive Classes: If checked, instructors may have back-to-back classes
- The generator will:
  - Respect instructor weekly hour limits
  - Avoid room conflicts
  - Consider break periods
  - Account for lab requirements

## Exporting Timetables

### Export to PDF
1. After generating a timetable, click "Export to PDF"
2. Choose the save location
3. Name your file
4. Click Save

### PDF Contents
The exported PDF will include:
- Complete weekly schedule
- Class timings
- Subject details
- Instructor assignments
- Room allocations

## Tips for Optimal Use

1. **Initial Setup**
   - Add all instructors first
   - Define subjects with accurate hours and durations
   - Set up appropriate time slots
   - Add all available rooms

2. **Class Management**
   - Create classes semester-wise
   - Assign subjects systematically
   - Ensure instructor availability

3. **Timetable Generation**
   - Verify all data before generating
   - Check for any conflicts in the generated timetable
   - Export and save important timetables

4. **Data Persistence**
   - All data is automatically saved
   - Changes are preserved between sessions
   - Regular backups are recommended

## Troubleshooting

### Common Issues and Solutions

1. **Unable to Generate Timetable**
   - Check if all subjects have instructors assigned
   - Verify instructor weekly hours are sufficient
   - Ensure enough rooms are available
   - Confirm time slots are properly set up

2. **Instructor Conflicts**
   - Review instructor weekly hour limits
   - Check for overlapping assignments
   - Verify subject-instructor assignments

3. **Room Allocation Issues**
   - Ensure sufficient rooms are available
   - Check if lab subjects are assigned appropriate rooms
   - Verify room availability during time slots

4. **Data Not Saving**
   - Check write permissions in the application directory
   - Ensure sufficient disk space
   - Verify no file system errors

For additional support or questions, please refer to the project documentation or contact the system administrator. 