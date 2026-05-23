package com.timetable.data;

import com.timetable.models.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataManager {
    private static final String DATA_FILE = "data.txt";
    private static AppData currentData = new AppData();

    public static AppData loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            setupDummyData();
            saveData();
            return currentData;
        }

        currentData = new AppData(); // Reset data
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String currentSection = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("[") && line.endsWith("]")) {
                    currentSection = line;
                    continue;
                }

                String[] parts = line.split("\\|");

                switch (currentSection) {
                    case "[ROOMS]":
                        if (parts.length >= 3) {
                            Room room = new Room(parts[0], parts[1], Boolean.parseBoolean(parts[2]));
                            currentData.getRooms().add(room);
                        }
                        break;
                    case "[ASSIGNMENTS]":
                        if (parts.length >= 7) {
                            String courseName = parts[0];
                            String teacherName = parts[1];
                            String labTeacherName = parts[2].equals("null") ? null : parts[2];
                            String batchName = parts[3];
                            String sectionName = parts[4];
                            int creditHours = Integer.parseInt(parts[5]);
                            boolean requiresLab = Boolean.parseBoolean(parts[6]);
                            
                            CourseAssignment ca = new CourseAssignment(courseName, teacherName, labTeacherName, batchName, sectionName, creditHours, requiresLab);
                            currentData.getAssignments().add(ca);
                        }
                        break;
                    case "[REPEATERS]":
                        if (parts.length >= 4) {
                            String studentName = parts[0];
                            String originalBatch = parts[1];
                            String originalSection = parts[2];
                            RepeaterStudent rs = new RepeaterStudent(studentName, originalBatch, originalSection);
                            
                            String[] courses = parts[3].split(",");
                            rs.getCoursesToRepeat().addAll(Arrays.asList(courses));
                            currentData.getRepeaterStudents().add(rs);
                        }
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading data file: " + e.getMessage());
        }

        return currentData;
    }

    public static void saveData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            
            // Save Rooms
            writer.write("[ROOMS]\n");
            for (Room r : currentData.getRooms()) {
                writer.write(r.getId() + "|" + r.getName() + "|" + r.isLab() + "\n");
            }
            writer.write("\n");


            writer.write("[ASSIGNMENTS]\n");
            for (CourseAssignment ca : currentData.getAssignments()) {
                String labTeacher = ca.getLabTeacherName() == null ? "null" : ca.getLabTeacherName();
                writer.write(ca.getCourseName() + "|" + ca.getTeacherName() + "|" + labTeacher + "|" 
                        + ca.getBatchName() + "|" + ca.getSectionName() + "|" 
                        + ca.getCreditHours() + "|" + ca.isRequiresLab() + "\n");
            }
            writer.write("\n");


            writer.write("[REPEATERS]\n");
            for (RepeaterStudent rs : currentData.getRepeaterStudents()) {
                String courses = String.join(",", rs.getCoursesToRepeat());
                writer.write(rs.getStudentName() + "|" + rs.getOriginalBatch() + "|" + rs.getOriginalSection() + "|" + courses + "\n");
            }
            writer.write("\n");

        } catch (IOException e) {
            System.err.println("Error saving data file: " + e.getMessage());
        }
    }

    public static AppData getCurrentData() {
        return currentData;
    }

    private static void setupDummyData() {

        Room r1 = new Room("R1", "Room 101", false);
        Room r2 = new Room("R2", "Lab 201", true);
        Room r3 = new Room("R3", "Room 102", false);
        Room r4 = new Room("R4", "Room 103", false);
        currentData.getRooms().add(r1);
        currentData.getRooms().add(r2);
        currentData.getRooms().add(r3);
        currentData.getRooms().add(r4);


    }
}
