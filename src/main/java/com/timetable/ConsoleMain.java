package com.timetable;

import com.timetable.data.AppData;
import com.timetable.data.DataManager;
import com.timetable.models.CourseAssignment;
import com.timetable.models.ScheduledClass;
import com.timetable.models.Timetable;
import com.timetable.scheduler.TimetableGenerator;

import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Backend-Only (CLI) runner with an interactive Scanner menu.
 * Proves MVC separation and allows manual data entry via the terminal.
 */
public class ConsoleMain {
    
    private static Timetable currentTimetable = null;
    // Stack for Undo feature (LIFO)
    private static Stack<CourseAssignment> undoStack = new Stack<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=========================================");
        System.out.println("  CLASH-FREE TIMETABLE GENERATOR (CLI)   ");
        System.out.println("=========================================");
        
        System.out.println("Loading data from data.txt...");
        AppData data = DataManager.loadData();
        
        while (true) {
            System.out.println("\n============= MAIN MENU =============");
            System.out.println("1. View All Registered Courses");
            System.out.println("2. Add New Course");
            System.out.println("3. Delete a Course (Includes Undo)");
            System.out.println("4. Generate Timetable");
            System.out.println("5. Search Timetable by Teacher");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    viewCoursesCLI(data);
                    break;
                case "2":
                    addCourseCLI(scanner, data);
                    break;
                case "3":
                    deleteCourseCLI(scanner, data);
                    break;
                case "4":
                    generateTimetableCLI(data);
                    break;
                case "5":
                    searchTeacherTimetableCLI(scanner);
                    break;
                case "6":
                    System.out.println("Exiting Console App. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 7.");
            }
        }
    }
    
    private static void viewCoursesCLI(AppData data) {
        System.out.println("\n--- Registered Courses ---");
        List<CourseAssignment> assignments = data.getAssignments();
        
        if (assignments.isEmpty()) {
            System.out.println("No courses currently registered.");
            return;
        }
        
        for (int i = 0; i < assignments.size(); i++) {
            CourseAssignment ca = assignments.get(i);
            System.out.printf("[%d] %s (Section %s) | Theory: %s | Lab: %s | Credits: %d%n",
                i + 1,
                ca.getCourseName(),
                ca.getSectionName(),
                ca.getTeacherName(),
                (ca.isRequiresLab() ? ca.getLabTeacherName() : "None"),
                ca.getCreditHours()
            );
        }
    }

    private static void addCourseCLI(Scanner scanner, AppData data) {
        System.out.println("\n--- Add New Course ---");
        
        System.out.print("Enter Course Name (e.g. Data Structures): ");
        String courseName = scanner.nextLine().trim();
        
        System.out.print("Enter Theory Teacher Name (e.g. Dr. Smith): ");
        String teacherName = scanner.nextLine().trim();
        
        System.out.print("Does it require a Lab? (yes/no): ");
        boolean requiresLab = scanner.nextLine().trim().equalsIgnoreCase("yes");
        
        String labTeacher = "null";
        if (requiresLab) {
            System.out.print("Enter Lab Teacher Name: ");
            labTeacher = scanner.nextLine().trim();
        }
        
        System.out.print("Enter Batch Name (e.g. SP25-BCS): ");
        String batchName = scanner.nextLine().trim();
        
        System.out.print("Enter Section (e.g. A, B, C): ");
        String sectionName = scanner.nextLine().trim();
        
        System.out.print("Enter Credit Hours (2, 3, or 4): ");
        int credits = 3;
        try {
            credits = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number, defaulting to 3 credits.");
        }
        
        CourseAssignment ca = new CourseAssignment(courseName, teacherName, labTeacher, batchName, sectionName, credits, requiresLab);
        data.getAssignments().add(ca);
        
        // Save to data.txt permanently
        DataManager.saveData();
        
        System.out.println("SUCCESS: Course added and saved to data.txt!");
    }
    
    private static void deleteCourseCLI(Scanner scanner, AppData data) {
        viewCoursesCLI(data);
        if (data.getAssignments().isEmpty() && undoStack.isEmpty()) return;
        
        System.out.print("\nEnter the number of the course to delete (or type 'undo' to restore the last deleted course, 0 to cancel): ");
        String input = scanner.nextLine().trim();
        
        if (input.equalsIgnoreCase("undo")) {
            undoDeleteCLI(data);
            return;
        }
        
        try {
            int index = Integer.parseInt(input) - 1;
            if (index == -1) {
                System.out.println("Deletion cancelled.");
                return;
            }
            if (index >= 0 && index < data.getAssignments().size()) {
                CourseAssignment removed = data.getAssignments().remove(index);
                // Push to Stack for Undo
                undoStack.push(removed);
                DataManager.saveData();
                System.out.println("SUCCESS: Removed course '" + removed.getCourseName() + "' and updated data.txt.");
            } else {
                System.out.println("Invalid number. Deletion failed.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Must be a number.");
        }
    }
    
    private static void undoDeleteCLI(AppData data) {
        if (undoStack.isEmpty()) {
            System.out.println("\nThe Undo Stack is empty. Nothing to undo!");
            return;
        }
        
        // LIFO: Pop the most recently deleted course
        CourseAssignment restoredCourse = undoStack.pop();
        data.getAssignments().add(restoredCourse);
        DataManager.saveData();
        System.out.println("\nSUCCESS: Undid deletion! Restored '" + restoredCourse.getCourseName() + "'.");
    }

    private static void generateTimetableCLI(AppData data) {
        System.out.println("\n--- Generating Timetable ---");
        
        if (data.getAssignments().isEmpty()) {
            System.out.println("WARNING: No courses found. Please add a course first!");
            return;
        }

        TimetableGenerator generator = new TimetableGenerator(data);
        
        try {
            System.out.println("Running DSA Constraint Satisfaction Engine...");
            long startTime = System.currentTimeMillis();
            currentTimetable = generator.generate(); // Store it globally for searching
            long endTime = System.currentTimeMillis();
            
            System.out.println("SUCCESS! Timetable generated without any clashes in " + (endTime - startTime) + "ms.\n");
            
            System.out.println("=========================================");
            System.out.println("           GENERATED TIMETABLE           ");
            System.out.println("=========================================");
            
            // Sort classes by Day, then Time
            currentTimetable.getClasses().sort((c1, c2) -> {
                int dayCmp = c1.getTimeslot().getDayOfWeek().compareTo(c2.getTimeslot().getDayOfWeek());
                if (dayCmp != 0) return dayCmp;
                return c1.getTimeslot().getStartTime().compareTo(c2.getTimeslot().getStartTime());
            });

            for (ScheduledClass sc : currentTimetable.getClasses()) {
                System.out.printf("%-10s | %-11s | %-12s | %-20s | %-10s | Teacher: %s%n",
                    sc.getTimeslot().getDayOfWeek(),
                    sc.getTimeslot().getStartTime() + "-" + sc.getTimeslot().getEndTime(),
                    sc.getSession().getSection().getName(),
                    sc.getSession().getSubject().getName(),
                    sc.getRoom().getName(),
                    sc.getSession().getTeacher().getName()
                );
            }
            
            if (!currentTimetable.getRepeaterSchedules().isEmpty()) {
                System.out.println("\n=========================================");
                System.out.println("         REPEATER STUDENT SCHEDULES      ");
                System.out.println("=========================================");
                
                currentTimetable.getRepeaterSchedules().forEach((student, classes) -> {
                    System.out.println("\nStudent: " + student.getStudentName() + " (Batch: " + student.getOriginalBatch() + "-" + student.getOriginalSection() + ")");
                    for (ScheduledClass sc : classes) {
                        System.out.printf("  -> %-10s %-5s | %-20s (Section %s)%n", 
                            sc.getTimeslot().getDayOfWeek(), 
                            sc.getTimeslot().getStartTime(), 
                            sc.getSession().getSubject().getName(), 
                            sc.getSession().getSection().getName()
                        );
                    }
                });
            }

        } catch (Exception e) {
            System.err.println("\nFAILED TO GENERATE TIMETABLE!");
            System.err.println(e.getMessage());
            currentTimetable = null;
        }
    }
    
    private static void searchTeacherTimetableCLI(Scanner scanner) {
        if (currentTimetable == null) {
            System.out.println("ERROR: You must Generate the Timetable (Option 4) before you can search it!");
            return;
        }
        
        System.out.print("\nEnter the exact Teacher Name to search for (e.g. Dr. Smith): ");
        String searchName = scanner.nextLine().trim();
        
        // Filter classes using Stream API
        List<ScheduledClass> teacherClasses = currentTimetable.getClasses().stream()
                .filter(c -> c.getSession().getTeacher().getName().equalsIgnoreCase(searchName))
                .collect(Collectors.toList());
                
        if (teacherClasses.isEmpty()) {
            System.out.println("No classes found for teacher: " + searchName);
            return;
        }
        
        System.out.println("\n--- Timetable for " + searchName + " ---");
        for (ScheduledClass sc : teacherClasses) {
            System.out.printf("%-10s | %-11s | %-20s | Section %-5s | Room: %s%n",
                sc.getTimeslot().getDayOfWeek(),
                sc.getTimeslot().getStartTime() + "-" + sc.getTimeslot().getEndTime(),
                sc.getSession().getSubject().getName(),
                sc.getSession().getSection().getName(),
                sc.getRoom().getName()
            );
        }
        System.out.println("------------------------------------");
    }
}
