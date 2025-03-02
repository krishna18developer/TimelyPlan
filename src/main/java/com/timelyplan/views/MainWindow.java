package com.timelyplan.views;

import com.timelyplan.controllers.TimetableGenerator;
import com.timelyplan.models.*;
import com.timelyplan.utils.DataManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class MainWindow extends JFrame {
    private TimetableGenerator generator;
    private JTabbedPane tabbedPane;
    private JTable timetableTable;
    private List<Instructor> instructors;
    private List<Subject> subjects;
    private List<TimeSlot> timeSlots;
    private DefaultListModel<String> roomsListModel;
    private List<CourseClass> classes;
    private ClassPanel classPanel;

    public MainWindow() {
        super("TimelyPlan - College Timetable Generator");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // Set application icon
        try {
            // Try multiple methods to load the icon
            java.net.URL iconURL = getClass().getResource("/images/timelyplan-icon.png");
            if (iconURL == null) {
                iconURL = getClass().getClassLoader().getResource("images/timelyplan-icon.png");
            }
            if (iconURL == null) {
                iconURL = ClassLoader.getSystemResource("images/timelyplan-icon.png");
            }
            
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                setIconImage(icon.getImage());
            } else {
                System.err.println("Could not find icon file: /images/timelyplan-icon.png");
            }
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
            e.printStackTrace();
        }
        
        setSize(1200, 800);
        setLocationRelativeTo(null);

        generator = new TimetableGenerator();
        loadData();
        initializeComponents();
        
        // Save data when closing the application
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    saveData();
                    System.out.println("Data saved successfully");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MainWindow.this,
                        "Error saving data: " + ex.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE);
                }
                dispose();
                System.exit(0);
            }
        });
    }

    private void loadData() {
        instructors = DataManager.loadInstructors();
        subjects = DataManager.loadSubjects();
        timeSlots = DataManager.loadTimeSlots();
        roomsListModel = new DefaultListModel<>();
        List<String> rooms = DataManager.loadRooms();
        rooms.forEach(room -> {
            roomsListModel.addElement(room);
            generator.addRoom(room, room.startsWith("L"));
        });
        classes = DataManager.loadClasses();
    }

    private void saveData() {
        DataManager.saveInstructors(instructors);
        DataManager.saveSubjects(subjects);
        DataManager.saveTimeSlots(timeSlots);
        List<String> rooms = new ArrayList<>();
        for (int i = 0; i < roomsListModel.size(); i++) {
            rooms.add(roomsListModel.get(i));
        }
        DataManager.saveRooms(rooms);
        DataManager.saveClasses(classPanel.getClasses());
    }

    private void saveDataAfterChange() {
        try {
            saveData();
            System.out.println("Data saved successfully after change");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error saving data: " + ex.getMessage(),
                "Save Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeComponents() {
        setTitle("TimelyPlan - College Timetable Generator");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Create tabs with improved styling
        tabbedPane.addTab("Instructors", createStyledPanel(createInstructorsPanel()));
        tabbedPane.addTab("Subjects", createStyledPanel(createSubjectsPanel()));
        tabbedPane.addTab("Time Slots", createStyledPanel(createTimeSlotsPanel()));
        tabbedPane.addTab("Rooms", createStyledPanel(createRoomsPanel()));
        
        // Initialize ClassPanel with save callback
        classPanel = new ClassPanel(classes, subjects, instructors, roomsListModel);
        classPanel.setSaveCallback(() -> saveData());
        tabbedPane.addTab("Classes", createStyledPanel(classPanel));
        
        tabbedPane.addTab("Generate", createStyledPanel(createGeneratePanel()));

        add(tabbedPane);
    }

    private JPanel createStyledPanel(JPanel content) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setFocusPainted(false);
        return button;
    }

    private JPanel createInstructorsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Instructor"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField nameField = new JTextField(20);
        JSpinner hoursSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 40, 1));
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Max Weekly Hours:"), gbc);
        gbc.gridx = 1;
        formPanel.add(hoursSpinner, gbc);
        
        JButton addButton = createStyledButton("Add Instructor");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(addButton, gbc);
        
        // List panel
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> instructorList = new JList<>(listModel);
        instructorList.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(instructorList);
        
        // Load existing instructors
        instructors.forEach(instructor -> 
            listModel.addElement(instructor.getName() + " (" + instructor.getMaxWeeklyHours() + " hrs/week)"));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton editButton = createStyledButton("Edit");
        JButton removeButton = createStyledButton("Remove");
        buttonsPanel.add(editButton);
        buttonsPanel.add(removeButton);
        
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            int hours = (Integer) hoursSpinner.getValue();
            
            if (!name.isEmpty()) {
                Instructor instructor = new Instructor(UUID.randomUUID().toString(), name, hours);
                instructors.add(instructor);
                listModel.addElement(name + " (" + hours + " hrs/week)");
                nameField.setText("");
                hoursSpinner.setValue(20);
                saveDataAfterChange();
            }
        });
        
        editButton.addActionListener(e -> {
            int selectedIndex = instructorList.getSelectedIndex();
            if (selectedIndex >= 0) {
                Instructor instructor = instructors.get(selectedIndex);
                String name = JOptionPane.showInputDialog(this, "Enter new name:", instructor.getName());
                if (name != null && !name.trim().isEmpty()) {
                    int hours = instructor.getMaxWeeklyHours();
                    try {
                        String hoursStr = JOptionPane.showInputDialog(this, 
                            "Enter new max weekly hours:", hours);
                        if (hoursStr != null) {
                            hours = Integer.parseInt(hoursStr);
                            instructor = new Instructor(instructor.getId(), name.trim(), hours);
                            instructors.set(selectedIndex, instructor);
                            listModel.set(selectedIndex, name + " (" + hours + " hrs/week)");
                            saveDataAfterChange();
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Invalid hours value", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        removeButton.addActionListener(e -> {
            int selectedIndex = instructorList.getSelectedIndex();
            if (selectedIndex >= 0) {
                instructors.remove(selectedIndex);
                listModel.remove(selectedIndex);
                saveDataAfterChange();
            }
        });
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createSubjectsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Subject"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField nameField = new JTextField(20);
        JSpinner hoursSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 20, 1));
        JCheckBox isLabCheckbox = new JCheckBox("Is Lab Session");
        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(45, 45, 180, 45));
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Weekly Hours:"), gbc);
        gbc.gridx = 1;
        formPanel.add(hoursSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Duration (minutes):"), gbc);
        gbc.gridx = 1;
        formPanel.add(durationSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(isLabCheckbox, gbc);
        
        JButton addButton = createStyledButton("Add Subject");
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(addButton, gbc);
        
        // List panel
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> subjectList = new JList<>(listModel);
        subjectList.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(subjectList);
        
        // Load existing subjects
        subjects.forEach(subject -> 
            listModel.addElement(subject.getName() + (subject.isLab() ? " (Lab)" : "") + 
                               " - " + subject.getWeeklyHours() + " hrs/week"));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton editButton = createStyledButton("Edit");
        JButton removeButton = createStyledButton("Remove");
        buttonsPanel.add(editButton);
        buttonsPanel.add(removeButton);
        
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            int hours = (Integer) hoursSpinner.getValue();
            int duration = (Integer) durationSpinner.getValue();
            boolean isLab = isLabCheckbox.isSelected();
            
            if (!name.isEmpty()) {
                Subject subject = new Subject(UUID.randomUUID().toString(), name, hours, isLab, duration);
                subjects.add(subject);
                listModel.addElement(name + (isLab ? " (Lab)" : "") + " - " + hours + " hrs/week");
                nameField.setText("");
                hoursSpinner.setValue(3);
                durationSpinner.setValue(55);
                isLabCheckbox.setSelected(false);
                saveDataAfterChange();
            }
        });
        
        editButton.addActionListener(e -> {
            int selectedIndex = subjectList.getSelectedIndex();
            if (selectedIndex >= 0) {
                Subject subject = subjects.get(selectedIndex);
                String name = JOptionPane.showInputDialog(this, "Enter new name:", subject.getName());
                if (name != null && !name.trim().isEmpty()) {
                    try {
                        String hoursStr = JOptionPane.showInputDialog(this, 
                            "Enter weekly hours (1-20):", subject.getWeeklyHours());
                        if (hoursStr != null) {
                            int hours = Integer.parseInt(hoursStr);
                            if (hours < 1 || hours > 20) {
                                throw new NumberFormatException("Hours must be between 1 and 20");
                            }
                            
                            String durationStr = JOptionPane.showInputDialog(this, 
                                "Enter duration in minutes (45-180):", subject.getDuration());
                            if (durationStr != null) {
                                int duration = Integer.parseInt(durationStr);
                                if (duration < 45 || duration > 180) {
                                    throw new NumberFormatException("Duration must be between 45 and 180 minutes");
                                }
                                
                                int isLabOption = JOptionPane.showConfirmDialog(this,
                                    "Is this a lab session?", "Lab Session",
                                    JOptionPane.YES_NO_OPTION);
                                boolean isLab = (isLabOption == JOptionPane.YES_OPTION);
                                
                                subject = new Subject(subject.getId(), name.trim(), hours, isLab, duration);
                                subjects.set(selectedIndex, subject);
                                listModel.set(selectedIndex, name + (isLab ? " (Lab)" : "") + 
                                            " - " + hours + " hrs/week");
                                saveDataAfterChange();
                            }
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        removeButton.addActionListener(e -> {
            int selectedIndex = subjectList.getSelectedIndex();
            if (selectedIndex >= 0) {
                subjects.remove(selectedIndex);
                listModel.remove(selectedIndex);
                saveDataAfterChange();
            }
        });
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createTimeSlotsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Time Slot"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Initialize spinners with current time
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 0);
        SpinnerDateModel startModel = new SpinnerDateModel(cal.getTime(), null, null, Calendar.MINUTE);
        
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 0);
        SpinnerDateModel endModel = new SpinnerDateModel(cal.getTime(), null, null, Calendar.MINUTE);
        
        JSpinner startSpinner = new JSpinner(startModel);
        JSpinner endSpinner = new JSpinner(endModel);
        
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startSpinner, "HH:mm");
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endSpinner, "HH:mm");
        startSpinner.setEditor(startEditor);
        endSpinner.setEditor(endEditor);
        
        String[] types = {"REGULAR", "BREAK", "LUNCH"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Start Time:"), gbc);
        gbc.gridx = 1;
        formPanel.add(startSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("End Time:"), gbc);
        gbc.gridx = 1;
        formPanel.add(endSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(typeCombo, gbc);
        
        JButton addButton = createStyledButton("Add Time Slot");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(addButton, gbc);
        
        // List panel with improved visibility
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> timeSlotList = new JList<>(listModel);
        timeSlotList.setFont(new Font("Arial", Font.PLAIN, 12));
        timeSlotList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(timeSlotList);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        
        // Load existing time slots and sort them by start time
        List<TimeSlot> sortedSlots = new ArrayList<>(timeSlots);
        sortedSlots.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));
        sortedSlots.forEach(slot -> listModel.addElement(slot.toString()));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton editButton = createStyledButton("Edit");
        JButton removeButton = createStyledButton("Remove");
        buttonsPanel.add(editButton);
        buttonsPanel.add(removeButton);
        
        addButton.addActionListener(e -> {
            Date startDate = startModel.getDate();
            Date endDate = endModel.getDate();
            String type = (String) typeCombo.getSelectedItem();
            
            LocalTime startTime = LocalTime.of(startDate.getHours(), startDate.getMinutes());
            LocalTime endTime = LocalTime.of(endDate.getHours(), endDate.getMinutes());
            
            if (endTime.isAfter(startTime)) {
                TimeSlot slot = new TimeSlot(startTime, endTime, 
                                           !type.equals("REGULAR"), type);
                timeSlots.add(slot);
                
                // Re-sort and update the list
                List<TimeSlot> newSortedSlots = new ArrayList<>(timeSlots);
                newSortedSlots.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));
                listModel.clear();
                newSortedSlots.forEach(s -> listModel.addElement(s.toString()));
                
                saveDataAfterChange();
            } else {
                JOptionPane.showMessageDialog(this,
                    "End time must be after start time",
                    "Invalid Time Range",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        editButton.addActionListener(e -> {
            int selectedIndex = timeSlotList.getSelectedIndex();
            if (selectedIndex >= 0) {
                TimeSlot slot = timeSlots.get(selectedIndex);
                
                // Set current values in spinners
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, slot.getStartTime().getHour());
                calendar.set(Calendar.MINUTE, slot.getStartTime().getMinute());
                startSpinner.setValue(calendar.getTime());
                
                calendar.set(Calendar.HOUR_OF_DAY, slot.getEndTime().getHour());
                calendar.set(Calendar.MINUTE, slot.getEndTime().getMinute());
                endSpinner.setValue(calendar.getTime());
                
                typeCombo.setSelectedItem(slot.getType());
                
                int result = JOptionPane.showConfirmDialog(this,
                    formPanel,
                    "Edit Time Slot",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
                
                if (result == JOptionPane.OK_OPTION) {
                    Date startDate = startModel.getDate();
                    Date endDate = endModel.getDate();
                    String type = (String) typeCombo.getSelectedItem();
                    
                    LocalTime startTime = LocalTime.of(startDate.getHours(), startDate.getMinutes());
                    LocalTime endTime = LocalTime.of(endDate.getHours(), endDate.getMinutes());
                    
                    if (endTime.isAfter(startTime)) {
                        TimeSlot newSlot = new TimeSlot(startTime, endTime, 
                                                      !type.equals("REGULAR"), type);
                        timeSlots.set(selectedIndex, newSlot);
                        
                        // Re-sort and update the list
                        List<TimeSlot> newSortedSlots = new ArrayList<>(timeSlots);
                        newSortedSlots.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));
                        listModel.clear();
                        newSortedSlots.forEach(s -> listModel.addElement(s.toString()));
                        
                        saveDataAfterChange();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "End time must be after start time",
                            "Invalid Time Range",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        removeButton.addActionListener(e -> {
            int selectedIndex = timeSlotList.getSelectedIndex();
            if (selectedIndex >= 0) {
                timeSlots.remove(selectedIndex);
                listModel.remove(selectedIndex);
                saveDataAfterChange();
            }
        });
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Room"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField roomField = new JTextField(20);
        JCheckBox isLabCheckbox = new JCheckBox("Is Lab Room");
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Room Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(roomField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        formPanel.add(isLabCheckbox, gbc);
        
        JButton addButton = createStyledButton("Add Room");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(addButton, gbc);
        
        // List panel
        JList<String> roomList = new JList<>(roomsListModel);
        roomList.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(roomList);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton editButton = createStyledButton("Edit");
        JButton removeButton = createStyledButton("Remove");
        buttonsPanel.add(editButton);
        buttonsPanel.add(removeButton);
        
        addButton.addActionListener(e -> {
            String room = roomField.getText().trim();
            if (!room.isEmpty()) {
                boolean isLab = isLabCheckbox.isSelected();
                if (isLab && !room.startsWith("L")) {
                    room = "L" + room;
                }
                generator.addRoom(room, isLab);
                roomsListModel.addElement(room);
                roomField.setText("");
                isLabCheckbox.setSelected(false);
                saveDataAfterChange();
            }
        });
        
        editButton.addActionListener(e -> {
            int selectedIndex = roomList.getSelectedIndex();
            if (selectedIndex >= 0) {
                String oldRoom = roomsListModel.getElementAt(selectedIndex);
                boolean isLab = oldRoom.startsWith("L");
                
                // Create a panel for the dialog
                JPanel dialogPanel = new JPanel(new GridLayout(2, 1));
                JTextField newRoomField = new JTextField(oldRoom);
                JCheckBox newIsLabCheckbox = new JCheckBox("Is Lab Room", isLab);
                dialogPanel.add(newRoomField);
                dialogPanel.add(newIsLabCheckbox);
                
                int result = JOptionPane.showConfirmDialog(this, dialogPanel, 
                    "Edit Room", JOptionPane.OK_CANCEL_OPTION);
                    
                if (result == JOptionPane.OK_OPTION) {
                    String newRoom = newRoomField.getText().trim();
                    boolean newIsLab = newIsLabCheckbox.isSelected();
                    
                    if (!newRoom.isEmpty()) {
                        if (newIsLab && !newRoom.startsWith("L")) {
                            newRoom = "L" + newRoom;
                        }
                        generator.addRoom(newRoom, newIsLab);
                        roomsListModel.set(selectedIndex, newRoom);
                        saveDataAfterChange();
                    }
                }
            }
        });
        
        removeButton.addActionListener(e -> {
            int selectedIndex = roomList.getSelectedIndex();
            if (selectedIndex >= 0) {
                roomsListModel.remove(selectedIndex);
                saveDataAfterChange();
            }
        });
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createGeneratePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        JCheckBox consecutiveClassesBox = new JCheckBox("Allow Consecutive Classes");
        consecutiveClassesBox.setSelected(true);
        
        // Add spinner for consecutive lab hours
        JSpinner labHoursSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 6, 1));
        JLabel labHoursLabel = new JLabel("Max Consecutive Lab Hours: ");
        
        JButton generateButton = new JButton("Generate Timetable");
        JButton exportButton = new JButton("Export to PDF");
        
        // Layout components
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        controlPanel.add(consecutiveClassesBox, gbc);
        
        gbc.gridy = 1; gbc.gridwidth = 1;
        controlPanel.add(labHoursLabel, gbc);
        gbc.gridx = 1;
        controlPanel.add(labHoursSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        controlPanel.add(generateButton, gbc);
        gbc.gridx = 1;
        controlPanel.add(exportButton, gbc);
        
        // Create table
        String[] columnNames = {"Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        timetableTable = new JTable(new DefaultTableModel(columnNames, 0));
        JScrollPane scrollPane = new JScrollPane(timetableTable);
        
        generateButton.addActionListener(e -> {
            try {
                // Create a new generator with the current settings
                generator = new TimetableGenerator();
                generator.setAllowConsecutiveClasses(consecutiveClassesBox.isSelected());
                generator.setMaxConsecutiveLabHours((Integer) labHoursSpinner.getValue());
                
                // Add all subjects
                for (Subject subject : subjects) {
                    generator.addSubject(subject);
                }
                
                // Add all instructors
                for (Instructor instructor : instructors) {
                    generator.addInstructor(instructor);
                }
                
                // Add all rooms with type information
                for (int i = 0; i < roomsListModel.size(); i++) {
                    String room = roomsListModel.getElementAt(i);
                    generator.addRoom(room, room.startsWith("L"));
                }
                
                // Add all classes and their room assignments
                for (CourseClass courseClass : classPanel.getClasses()) {
                    generator.addCourseClass(courseClass);
                }
                
                // Set time slots
                generator.setTimeSlots(timeSlots);
                
                // Generate timetable
                Map<String, List<TimetableEntry>> timetable = generator.generateTimetable();
                
                // Update table
                updateTimetableDisplay(timetable);
                
            } catch (Exception ex) {
                ex.printStackTrace(); // Add this to see the full error stack trace
                JOptionPane.showMessageDialog(this,
                    "Error generating timetable: " + ex.getMessage(),
                    "Generation Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        exportButton.addActionListener(e -> exportToPDF());
        
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private void updateTimetableDisplay(Map<String, List<TimetableEntry>> timetable) {
        // Sort time slots
        List<TimeSlot> sortedSlots = new ArrayList<>(timeSlots);
        sortedSlots.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));

        // Create column headers with time slots
        String[] columnNames = new String[sortedSlots.size() + 1];
        columnNames[0] = "Day";
        for (int i = 0; i < sortedSlots.size(); i++) {
            TimeSlot slot = sortedSlots.get(i);
            columnNames[i + 1] = String.format("%s-%s", 
                slot.getStartTime().toString(), 
                slot.getEndTime().toString());
        }

        // Create the table model with the new column structure
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        timetableTable.setModel(model);

        // Add data for each day
        List<String> days = Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY");
        for (String day : days) {
            Object[] row = new Object[sortedSlots.size() + 1];
            row[0] = day;
            
            List<TimetableEntry> dayEntries = timetable.get(day);
            for (int i = 0; i < sortedSlots.size(); i++) {
                TimeSlot slot = sortedSlots.get(i);
                String cellContent = "";
                
                for (TimetableEntry entry : dayEntries) {
                    if (entry.getTimeSlot().equals(slot)) {
                        cellContent = String.format("<html><div style='text-align: center;'><b>%s</b><br>%s<br>%s</div></html>",
                            entry.getSubject().getName(),
                            entry.getInstructor().getName(),
                            entry.getRoom());
                        break;
                    }
                }
                
                row[i + 1] = cellContent;
            }
            
            model.addRow(row);
        }

        // Set table appearance
        timetableTable.setRowHeight(80);
        timetableTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        for (int i = 1; i < timetableTable.getColumnCount(); i++) {
            timetableTable.getColumnModel().getColumn(i).setPreferredWidth(150);
        }
        
        // Center align all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < timetableTable.getColumnCount(); i++) {
            timetableTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void exportToPDF() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("timetable.pdf"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                PDDocument document = new PDDocument();
                PDPage page = new PDPage(new PDRectangle(PDRectangle.A3.getHeight(), PDRectangle.A3.getWidth()));
                document.addPage(page);
                
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                
                // Add title
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
                contentStream.newLineAtOffset(50, page.getMediaBox().getWidth() - 50);
                contentStream.showText("College Timetable");
                contentStream.endText();
                
                // Calculate table dimensions
                float margin = 50;
                float yStart = page.getMediaBox().getWidth() - 100;
                float tableWidth = page.getMediaBox().getHeight() - 2 * margin;
                float yPosition = yStart;
                float rowHeight = 60;
                float tableHeight = rowHeight * (timetableTable.getRowCount() + 1);
                float colWidth = (tableWidth - 100) / (timetableTable.getColumnCount() - 1);
                
                // Draw table grid
                // First, draw the day column (narrower)
                contentStream.setLineWidth(1f);
                contentStream.moveTo(margin, yStart);
                contentStream.lineTo(margin, yStart - tableHeight);
                contentStream.moveTo(margin + 100, yStart);
                contentStream.lineTo(margin + 100, yStart - tableHeight);
                contentStream.stroke();
                
                // Draw horizontal lines
                for (int i = 0; i <= timetableTable.getRowCount(); i++) {
                    contentStream.moveTo(margin, yPosition);
                    contentStream.lineTo(margin + tableWidth, yPosition);
                    contentStream.stroke();
                    yPosition -= rowHeight;
                }
                
                // Draw vertical lines for time slots
                float xPosition = margin + 100;
                for (int i = 0; i <= timetableTable.getColumnCount() - 1; i++) {
                    contentStream.moveTo(xPosition, yStart);
                    contentStream.lineTo(xPosition, yStart - tableHeight);
                    contentStream.stroke();
                    xPosition += colWidth;
                }
                
                // Add headers
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                float xText = margin + 10;
                float yText = yStart - 15;
                
                // Add "Day" header
                contentStream.beginText();
                contentStream.newLineAtOffset(xText, yText);
                contentStream.showText("Day");
                contentStream.endText();
                
                // Add time slot headers
                xText = margin + 110;
                for (int i = 1; i < timetableTable.getColumnCount(); i++) {
                    String header = timetableTable.getColumnName(i);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(xText, yText);
                    contentStream.showText(header);
                    contentStream.endText();
                    xText += colWidth;
                }
                
                // Add table content
                contentStream.setFont(PDType1Font.HELVETICA, 9);
                yText = yStart - rowHeight - 15;
                
                for (int row = 0; row < timetableTable.getRowCount(); row++) {
                    // Add day
                    xText = margin + 10;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(xText, yText);
                    contentStream.showText(timetableTable.getValueAt(row, 0).toString());
                    contentStream.endText();
                    
                    // Add entries
                    xText = margin + 110;
                    for (int col = 1; col < timetableTable.getColumnCount(); col++) {
                        String cellContent = String.valueOf(timetableTable.getValueAt(row, col))
                            .replace("<html><div style='text-align: center;'>", "")
                            .replace("</div></html>", "")
                            .replace("<br>", " - ")
                            .replace("<b>", "")
                            .replace("</b>", "");
                            
                        contentStream.beginText();
                        contentStream.newLineAtOffset(xText, yText);
                        contentStream.showText(cellContent);
                        contentStream.endText();
                        xText += colWidth;
                    }
                    yText -= rowHeight;
                }
                
                contentStream.close();
                document.save(fileChooser.getSelectedFile());
                document.close();
                
                JOptionPane.showMessageDialog(this,
                    "Timetable exported successfully!",
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error exporting to PDF: " + ex.getMessage(),
                "Export Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainWindow().setVisible(true);
        });
    }
} 