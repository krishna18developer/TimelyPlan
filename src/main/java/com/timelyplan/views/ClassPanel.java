package com.timelyplan.views;

import com.timelyplan.models.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ClassPanel extends JPanel {
    private List<CourseClass> classes;
    private List<Subject> subjects;
    private List<Instructor> instructors;
    private DefaultListModel<String> classListModel;
    private JList<String> classList;
    private JTable subjectInstructorTable;
    private DefaultListModel<String> availableSubjectsModel;
    private DefaultListModel<String> assignedSubjectsModel;

    public ClassPanel(List<CourseClass> classes, List<Subject> subjects, List<Instructor> instructors) {
        this.classes = classes;
        this.subjects = subjects;
        this.instructors = instructors;
        setLayout(new BorderLayout(10, 10));
        initializeComponents();
    }

    private void initializeComponents() {
        // Left panel for class list and add/edit/remove buttons
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Classes"));

        classListModel = new DefaultListModel<>();
        classList = new JList<>(classListModel);
        classList.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane classScrollPane = new JScrollPane(classList);

        // Load existing classes
        for (CourseClass cls : classes) {
            classListModel.addElement(cls.toString());
        }

        JPanel classButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addClass = new JButton("Add Class");
        JButton editClass = new JButton("Edit Class");
        JButton removeClass = new JButton("Remove Class");

        classButtonPanel.add(addClass);
        classButtonPanel.add(editClass);
        classButtonPanel.add(removeClass);

        leftPanel.add(classScrollPane, BorderLayout.CENTER);
        leftPanel.add(classButtonPanel, BorderLayout.SOUTH);

        // Right panel for subject-instructor assignments
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Subject-Instructor Assignments"));

        // Create the assignment panel
        JPanel assignmentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Available subjects list
        gbc.gridx = 0; gbc.gridy = 0;
        assignmentPanel.add(new JLabel("Available Subjects:"), gbc);

        availableSubjectsModel = new DefaultListModel<>();
        JList<String> availableSubjectsList = new JList<>(availableSubjectsModel);
        JScrollPane availableScrollPane = new JScrollPane(availableSubjectsList);
        gbc.gridy = 1; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        assignmentPanel.add(availableScrollPane, gbc);

        // Assigned subjects list
        gbc.gridx = 2; gbc.gridy = 0; gbc.weighty = 0.0;
        assignmentPanel.add(new JLabel("Assigned Subjects:"), gbc);

        assignedSubjectsModel = new DefaultListModel<>();
        JList<String> assignedSubjectsList = new JList<>(assignedSubjectsModel);
        JScrollPane assignedScrollPane = new JScrollPane(assignedSubjectsList);
        gbc.gridy = 1; gbc.weighty = 1.0;
        assignmentPanel.add(assignedScrollPane, gbc);

        // Assignment buttons
        JPanel assignButtonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JButton assignButton = new JButton(">>");
        JButton unassignButton = new JButton("<<");
        assignButtonPanel.add(assignButton);
        assignButtonPanel.add(unassignButton);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weighty = 0.0;
        assignmentPanel.add(assignButtonPanel, gbc);

        // Instructor selection for assigned subjects
        JPanel instructorPanel = new JPanel(new BorderLayout(5, 5));
        instructorPanel.setBorder(BorderFactory.createTitledBorder("Assign Instructor"));

        JComboBox<String> instructorCombo = new JComboBox<>();
        for (Instructor instructor : instructors) {
            instructorCombo.addItem(instructor.getName());
        }

        JButton assignInstructorButton = new JButton("Assign Instructor");
        instructorPanel.add(instructorCombo, BorderLayout.CENTER);
        instructorPanel.add(assignInstructorButton, BorderLayout.EAST);

        rightPanel.add(assignmentPanel, BorderLayout.CENTER);
        rightPanel.add(instructorPanel, BorderLayout.SOUTH);

        // Add action listeners
        addClass.addActionListener(e -> addNewClass());
        editClass.addActionListener(e -> editSelectedClass());
        removeClass.addActionListener(e -> removeSelectedClass());

        classList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSubjectLists();
            }
        });

        assignButton.addActionListener(e -> assignSubject(availableSubjectsList.getSelectedValue()));
        unassignButton.addActionListener(e -> unassignSubject(assignedSubjectsList.getSelectedValue()));
        assignInstructorButton.addActionListener(e -> assignInstructorToSubject(
            assignedSubjectsList.getSelectedValue(),
            instructorCombo.getSelectedItem().toString()
        ));

        // Add panels to main panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);
    }

    private void addNewClass() {
        String name = JOptionPane.showInputDialog(this, "Enter class name (e.g., CSE-A):");
        if (name != null && !name.trim().isEmpty()) {
            try {
                String semesterStr = JOptionPane.showInputDialog(this, "Enter semester (1-8):");
                if (semesterStr != null) {
                    int semester = Integer.parseInt(semesterStr);
                    if (semester >= 1 && semester <= 8) {
                        CourseClass newClass = new CourseClass(UUID.randomUUID().toString(), name.trim(), semester);
                        classes.add(newClass);
                        classListModel.addElement(newClass.toString());
                    } else {
                        JOptionPane.showMessageDialog(this, "Semester must be between 1 and 8");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid semester number");
            }
        }
    }

    private void editSelectedClass() {
        int selectedIndex = classList.getSelectedIndex();
        if (selectedIndex >= 0) {
            CourseClass selectedClass = classes.get(selectedIndex);
            String name = JOptionPane.showInputDialog(this, "Enter new class name:", selectedClass.getName());
            if (name != null && !name.trim().isEmpty()) {
                try {
                    String semesterStr = JOptionPane.showInputDialog(this, 
                        "Enter new semester (1-8):", selectedClass.getSemester());
                    if (semesterStr != null) {
                        int semester = Integer.parseInt(semesterStr);
                        if (semester >= 1 && semester <= 8) {
                            CourseClass updatedClass = new CourseClass(selectedClass.getId(), name.trim(), semester);
                            updatedClass.setSubjectToInstructorMap(selectedClass.getSubjectToInstructorMap());
                            classes.set(selectedIndex, updatedClass);
                            classListModel.set(selectedIndex, updatedClass.toString());
                        } else {
                            JOptionPane.showMessageDialog(this, "Semester must be between 1 and 8");
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid semester number");
                }
            }
        }
    }

    private void removeSelectedClass() {
        int selectedIndex = classList.getSelectedIndex();
        if (selectedIndex >= 0) {
            classes.remove(selectedIndex);
            classListModel.remove(selectedIndex);
            updateSubjectLists();
        }
    }

    private void updateSubjectLists() {
        availableSubjectsModel.clear();
        assignedSubjectsModel.clear();

        int selectedIndex = classList.getSelectedIndex();
        if (selectedIndex >= 0) {
            CourseClass selectedClass = classes.get(selectedIndex);
            Set<String> assignedSubjectIds = selectedClass.getSubjectToInstructorMap().keySet();

            for (Subject subject : subjects) {
                if (!assignedSubjectIds.contains(subject.getId())) {
                    availableSubjectsModel.addElement(subject.getName());
                } else {
                    String instructorId = selectedClass.getInstructorForSubject(subject.getId());
                    String instructorName = getInstructorName(instructorId);
                    assignedSubjectsModel.addElement(subject.getName() + " (" + instructorName + ")");
                }
            }
        }
    }

    private void assignSubject(String subjectName) {
        if (subjectName != null) {
            int selectedClassIndex = classList.getSelectedIndex();
            if (selectedClassIndex >= 0) {
                CourseClass selectedClass = classes.get(selectedClassIndex);
                Subject subject = findSubjectByName(subjectName);
                if (subject != null) {
                    selectedClass.assignInstructorToSubject(subject.getId(), null);
                    updateSubjectLists();
                }
            }
        }
    }

    private void unassignSubject(String subjectEntry) {
        if (subjectEntry != null) {
            int selectedClassIndex = classList.getSelectedIndex();
            if (selectedClassIndex >= 0) {
                CourseClass selectedClass = classes.get(selectedClassIndex);
                String subjectName = subjectEntry.split(" \\(")[0];
                Subject subject = findSubjectByName(subjectName);
                if (subject != null) {
                    selectedClass.removeSubject(subject.getId());
                    updateSubjectLists();
                }
            }
        }
    }

    private void assignInstructorToSubject(String subjectEntry, String instructorName) {
        if (subjectEntry != null && instructorName != null) {
            int selectedClassIndex = classList.getSelectedIndex();
            if (selectedClassIndex >= 0) {
                CourseClass selectedClass = classes.get(selectedClassIndex);
                String subjectName = subjectEntry.split(" \\(")[0];
                Subject subject = findSubjectByName(subjectName);
                Instructor instructor = findInstructorByName(instructorName);
                if (subject != null && instructor != null) {
                    selectedClass.assignInstructorToSubject(subject.getId(), instructor.getId());
                    updateSubjectLists();
                }
            }
        }
    }

    private Subject findSubjectByName(String name) {
        return subjects.stream()
            .filter(s -> s.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

    private Instructor findInstructorByName(String name) {
        return instructors.stream()
            .filter(i -> i.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

    private String getInstructorName(String instructorId) {
        if (instructorId == null) return "Unassigned";
        return instructors.stream()
            .filter(i -> i.getId().equals(instructorId))
            .map(Instructor::getName)
            .findFirst()
            .orElse("Unknown");
    }

    public List<CourseClass> getClasses() {
        return new ArrayList<>(classes);
    }
} 