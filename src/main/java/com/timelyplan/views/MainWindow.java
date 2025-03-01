package com.timelyplan.views;

import com.timelyplan.controllers.TimetableGenerator;
import com.timelyplan.models.*;
import com.timelyplan.utils.DataManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

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
            generator.addRoom(room);
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
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Create tabs with improved styling
        tabbedPane.addTab("Instructors", createStyledPanel(createInstructorsPanel()));
        tabbedPane.addTab("Subjects", createStyledPanel(createSubjectsPanel()));
        tabbedPane.addTab("Time Slots", createStyledPanel(createTimeSlotsPanel()));
        tabbedPane.addTab("Rooms", createStyledPanel(createRoomsPanel()));
        classPanel = new ClassPanel(classes, subjects, instructors);
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
                durationSpinner.setValue(45);
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
        
        SpinnerDateModel startModel = new SpinnerDateModel();
        SpinnerDateModel endModel = new SpinnerDateModel();
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
        
        // List panel
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> timeSlotList = new JList<>(listModel);
        timeSlotList.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(timeSlotList);
        
        // Load existing time slots
        timeSlots.forEach(slot -> listModel.addElement(slot.toString()));
        
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
                listModel.addElement(slot.toString());
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
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, slot.getStartTime().getHour());
                cal.set(Calendar.MINUTE, slot.getStartTime().getMinute());
                startSpinner.setValue(cal.getTime());
                
                cal.set(Calendar.HOUR_OF_DAY, slot.getEndTime().getHour());
                cal.set(Calendar.MINUTE, slot.getEndTime().getMinute());
                endSpinner.setValue(cal.getTime());
                
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
                        listModel.set(selectedIndex, newSlot.toString());
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
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Room Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(roomField, gbc);
        
        JButton addButton = createStyledButton("Add Room");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
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
                generator.addRoom(room);
                roomsListModel.addElement(room);
                roomField.setText("");
                saveDataAfterChange();
            }
        });
        
        editButton.addActionListener(e -> {
            int selectedIndex = roomList.getSelectedIndex();
            if (selectedIndex >= 0) {
                String oldRoom = roomsListModel.getElementAt(selectedIndex);
                String newRoom = JOptionPane.showInputDialog(this, 
                    "Enter new room name:", oldRoom);
                if (newRoom != null && !newRoom.trim().isEmpty()) {
                    generator.addRoom(newRoom.trim());
                    roomsListModel.set(selectedIndex, newRoom.trim());
                    saveDataAfterChange();
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
        
        JPanel controlPanel = new JPanel(new FlowLayout());
        JCheckBox consecutiveClassesBox = new JCheckBox("Allow Consecutive Classes");
        consecutiveClassesBox.setSelected(true);
        JButton generateButton = new JButton("Generate Timetable");
        JButton exportButton = new JButton("Export to PDF");
        
        controlPanel.add(consecutiveClassesBox);
        controlPanel.add(generateButton);
        controlPanel.add(exportButton);
        
        // Create table
        String[] columnNames = {"Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        timetableTable = new JTable(new DefaultTableModel(columnNames, 0));
        JScrollPane scrollPane = new JScrollPane(timetableTable);
        
        generateButton.addActionListener(e -> {
            try {
                generator.setAllowConsecutiveClasses(consecutiveClassesBox.isSelected());
                
                // Add all data to generator
                for (Subject subject : subjects) {
                    generator.addSubject(subject);
                }
                
                for (Instructor instructor : instructors) {
                    generator.addInstructor(instructor);
                }
                
                generator.setTimeSlots(timeSlots);
                
                // Generate timetable
                Map<String, List<TimetableEntry>> timetable = generator.generateTimetable();
                
                // Update table
                updateTimetableDisplay(timetable);
                
            } catch (Exception ex) {
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
        DefaultTableModel model = (DefaultTableModel) timetableTable.getModel();
        model.setRowCount(0);
        
        // Sort time slots
        List<TimeSlot> sortedSlots = new ArrayList<>(timeSlots);
        sortedSlots.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));
        
        for (TimeSlot slot : sortedSlots) {
            Object[] row = new Object[7];
            row[0] = slot.toString();
            
            int col = 1;
            for (String day : Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY")) {
                List<TimetableEntry> entries = timetable.get(day);
                String cellContent = "";
                
                for (TimetableEntry entry : entries) {
                    if (entry.getTimeSlot().equals(slot)) {
                        cellContent = entry.getSubject().getName() + "\n" +
                                    entry.getInstructor().getName() + "\n" +
                                    entry.getRoom();
                        break;
                    }
                }
                
                row[col++] = cellContent;
            }
            
            model.addRow(row);
        }
    }

    private void exportToPDF() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("timetable.pdf"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                PDDocument document = new PDDocument();
                PDPage page = new PDPage();
                document.addPage(page);
                
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                
                // Add title
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("College Timetable");
                contentStream.endText();
                
                // Add table content
                float y = 700;
                float margin = 50;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float rowHeight = 20;
                float tableHeight = rowHeight * (timetableTable.getRowCount() + 1);
                float cellMargin = 5;
                
                // Draw table grid
                float nextY = y;
                for (int row = 0; row <= timetableTable.getRowCount(); row++) {
                    contentStream.moveTo(margin, nextY);
                    contentStream.lineTo(margin + tableWidth, nextY);
                    contentStream.stroke();
                    nextY -= rowHeight;
                }
                
                float colWidth = tableWidth / timetableTable.getColumnCount();
                float nextX = margin;
                for (int col = 0; col <= timetableTable.getColumnCount(); col++) {
                    contentStream.moveTo(nextX, y);
                    contentStream.lineTo(nextX, y - tableHeight);
                    contentStream.stroke();
                    nextX += colWidth;
                }
                
                // Add text content
                contentStream.setFont(PDType1Font.HELVETICA, 8);
                float textY = y - 15;
                
                for (int row = 0; row < timetableTable.getRowCount(); row++) {
                    float textX = margin + cellMargin;
                    
                    for (int col = 0; col < timetableTable.getColumnCount(); col++) {
                        String text = String.valueOf(timetableTable.getValueAt(row, col));
                        contentStream.beginText();
                        contentStream.newLineAtOffset(textX, textY);
                        contentStream.showText(text.replace("\n", " - "));
                        contentStream.endText();
                        textX += colWidth;
                    }
                    
                    textY -= rowHeight;
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