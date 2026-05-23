package com.timetable.scheduler;

import com.timetable.data.AppData;
import com.timetable.models.*;

import java.util.*;
import java.util.stream.Collectors;

public class TimetableGenerator {

    private AppData data;
    private List<Timeslot> allTimeslots;
    private ConflictReporter reporter;

    public TimetableGenerator(AppData data) {
        this.data = data;
        this.allTimeslots = generateStandardTimeslots();
        this.reporter = new ConflictReporter();
    }

    private List<Timeslot> generateStandardTimeslots() {
        List<Timeslot> slots = new ArrayList<>();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        
        for (String day : days) {
            int startHour = 8;
            int startMin = 30;
            
            while (startHour < 17) {
                int endHour = startHour;
                int endMin = startMin + 30;
                if (endMin >= 60) {
                    endHour++;
                    endMin -= 60;
                }
                
                String start = String.format("%02d:%02d", startHour, startMin);
                String end = String.format("%02d:%02d", endHour, endMin);
                slots.add(new Timeslot(day, start, end));
                
                startHour = endHour;
                startMin = endMin;
            }
        }
        return slots;
    }

    public Timetable generate() throws Exception {
        Timetable timetable = new Timetable();
        List<Session> sessionsToSchedule = createSessions();
        

        sessionsToSchedule.sort((s1, s2) -> Integer.compare(s2.getConsecutiveSlots(), s1.getConsecutiveSlots()));

        boolean success = backtrack(sessionsToSchedule, 0, timetable);
        
        if (!success) {
            String explanation = reporter.getTopConflictsReport();
            throw new Exception("Could not generate a clash-free timetable.\n\nConflict Explanation Engine Report:\n" + explanation);
        }

        scheduleRepeaters(timetable);
        
        return timetable;
    }

    private List<Session> createSessions() {
        List<Session> sessions = new ArrayList<>();
        for (CourseAssignment ca : data.getAssignments()) {
            Subject subjectTheory = new Subject(ca.getCourseName(), ca.getCourseName(), ca.getCreditHours(), ca.isRequiresLab());
            Teacher teacherTheory = new Teacher(ca.getTeacherName(), ca.getTeacherName());
            Batch batch = new Batch(ca.getBatchName(), ca.getBatchName());
            String fullSectionName = ca.getBatchName() + "-" + ca.getSectionName();
            Section section = new Section(fullSectionName, fullSectionName);
            section.setBatch(batch);

            if (ca.getCreditHours() == 2) {

                sessions.add(new Session(subjectTheory, teacherTheory, section, false, 1.0, 2));
                sessions.add(new Session(subjectTheory, teacherTheory, section, false, 1.0, 2));
            } else if (ca.getCreditHours() == 3) {
                if (ca.isRequiresLab()) {

                    sessions.add(new Session(subjectTheory, teacherTheory, section, false, 1.0, 2));
                    sessions.add(new Session(subjectTheory, teacherTheory, section, false, 1.0, 2));
                    
                    Subject subjectLab = new Subject(ca.getCourseName() + " Lab", ca.getCourseName() + " Lab", 1, true);
                    Teacher teacherLab = new Teacher(ca.getLabTeacherName() != null ? ca.getLabTeacherName() : ca.getTeacherName(), ca.getLabTeacherName() != null ? ca.getLabTeacherName() : ca.getTeacherName());
                    sessions.add(new Session(subjectLab, teacherLab, section, true, 3.0, 6)); 
                } else {
                    // 2 theory classes, 1.5 hours each (3 blocks)
                    sessions.add(new Session(subjectTheory, teacherTheory, section, false, 1.5, 3));
                    sessions.add(new Session(subjectTheory, teacherTheory, section, false, 1.5, 3));
                }
            } else if (ca.getCreditHours() == 4) {

                sessions.add(new Session(subjectTheory, teacherTheory, section, false, 1.5, 3));
                sessions.add(new Session(subjectTheory, teacherTheory, section, false, 1.5, 3));
                
                Subject subjectLab = new Subject(ca.getCourseName() + " Lab", ca.getCourseName() + " Lab", 1, true);
                Teacher teacherLab = new Teacher(ca.getLabTeacherName() != null ? ca.getLabTeacherName() : ca.getTeacherName(), ca.getLabTeacherName() != null ? ca.getLabTeacherName() : ca.getTeacherName());
                sessions.add(new Session(subjectLab, teacherLab, section, true, 3.0, 6)); 
            }
        }
        return sessions;
    }

    private boolean backtrack(List<Session> sessions, int index, Timetable timetable) {
        if (index == sessions.size()) {
            return true;
        }

        Session session = sessions.get(index);
        
        List<Timeslot> availableSlots = new ArrayList<>(allTimeslots);
        Collections.shuffle(availableSlots);
        
        List<Room> availableRooms = new ArrayList<>(data.getRooms());
        Collections.shuffle(availableRooms);

        int neededBlocks = session.getConsecutiveSlots();

        for (int i = 0; i < availableSlots.size(); i++) {
            Timeslot timeslot = availableSlots.get(i);
            int originalIndex = allTimeslots.indexOf(timeslot);

            // Check if there are enough continuous slots left in the day
            if (originalIndex + neededBlocks > allTimeslots.size()) {
                continue;
            }


            List<Timeslot> blockSlots = new ArrayList<>();
            boolean crossDayBoundary = false;
            String baseDay = timeslot.getDayOfWeek();
            
            for (int b = 0; b < neededBlocks; b++) {
                Timeslot ts = allTimeslots.get(originalIndex + b);
                if (!ts.getDayOfWeek().equals(baseDay)) {
                    crossDayBoundary = true;
                    break;
                }
                blockSlots.add(ts);
            }

            if (crossDayBoundary) {
                continue; // Class must be finished within the same day
            }

            for (Room room : availableRooms) {
                if (isValid(session, blockSlots, room, timetable)) {
                    List<ScheduledClass> addedClasses = new ArrayList<>();
                    for (Timeslot ts : blockSlots) {
                        ScheduledClass sc = new ScheduledClass(session, ts, room);
                        timetable.addClass(sc);
                        addedClasses.add(sc);
                    }

                    if (backtrack(sessions, index + 1, timetable)) {
                        return true;
                    }


                    for (ScheduledClass sc : addedClasses) {
                        timetable.getClasses().remove(sc);
                    }
                }
            }
        }

        return false;
    }

    private boolean isValid(Session session, List<Timeslot> blockSlots, Room room, Timetable timetable) {
        if (session.isLab() && !room.isLab()) {
            reporter.log("Room " + room.getName() + " is not a Lab");
            return false;
        }
        if (!session.isLab() && room.isLab()) {
            reporter.log("Room " + room.getName() + " is a Lab but course is Theory");
            return false;
        }

        for (Timeslot timeslot : blockSlots) {
            for (ScheduledClass sc : timetable.getClasses()) {
                if (sc.getTimeslot().equals(timeslot)) {
                    if (sc.getRoom().equals(room)) {
                        reporter.log("Room " + room.getName() + " already booked at " + timeslot);
                        return false;
                    }
                    if (sc.getSession().getTeacher().equals(session.getTeacher())) {
                        reporter.log("Teacher " + session.getTeacher().getName() + " busy at " + timeslot);
                        return false;
                    }
                    if (sc.getSession().getSection().equals(session.getSection())) {
                        reporter.log("Section " + session.getSection().getName() + " already has a class at " + timeslot);
                        return false;
                    }
                }
                
                // Limit theory classes: mostly 1 session of same subject per day for a section
                if (sc.getSession().getSubject().equals(session.getSubject()) &&
                    sc.getSession().getSection().equals(session.getSection()) &&
                    sc.getTimeslot().getDayOfWeek().equals(timeslot.getDayOfWeek())) {
                    // If it is the SAME session instance (spanning blocks), it's fine.
                    if (sc.getSession() != session && !session.isLab()) { 
                        reporter.log("Section " + session.getSection().getName() + " already has " + session.getSubject().getName() + " on " + timeslot.getDayOfWeek());
                        return false;
                    }
                }
            }
        }
        
        // --- MAXIMUM 1 HOUR GAP CONSTRAINT (DSA Logic) ---
        String targetDay = blockSlots.get(0).getDayOfWeek();
        List<Timeslot> sectionDaySlots = new ArrayList<>();
        
        for (ScheduledClass sc : timetable.getClasses()) {
            if (sc.getSession().getSection().equals(session.getSection()) && 
                sc.getTimeslot().getDayOfWeek().equals(targetDay)) {
                sectionDaySlots.add(sc.getTimeslot());
            }
        }

        if (!sectionDaySlots.isEmpty()) {
            List<Timeslot> mergedSlots = new ArrayList<>(sectionDaySlots);
            mergedSlots.addAll(blockSlots);
            

            mergedSlots.sort(Comparator.comparingInt(ts -> allTimeslots.indexOf(ts)));
            

            for (int i = 0; i < mergedSlots.size() - 1; i++) {
                int idx1 = allTimeslots.indexOf(mergedSlots.get(i));
                int idx2 = allTimeslots.indexOf(mergedSlots.get(i+1));
                

                if (idx2 - idx1 > 3) {
                    reporter.log("Gap for section " + session.getSection().getName() + " exceeds 1 hour on " + targetDay);
                    return false;
                }
            }
        }

        return true;
    }

    private void scheduleRepeaters(Timetable timetable) {
        for (RepeaterStudent student : data.getRepeaterStudents()) {
            String originalFullSection = student.getOriginalBatch() + "-" + student.getOriginalSection();
            
            List<ScheduledClass> studentSchedule = new ArrayList<>();
            for (ScheduledClass sc : timetable.getClasses()) {
                if (sc.getSession().getSection().getName().equals(originalFullSection)) {
                    studentSchedule.add(sc);
                }
            }

            for (String courseToRepeat : student.getCoursesToRepeat()) {
                Map<String, List<ScheduledClass>> sectionsOfferingCourse = new HashMap<>();
                for (ScheduledClass sc : timetable.getClasses()) {
                    if (sc.getSession().getSubject().getName().equalsIgnoreCase(courseToRepeat)) {
                        sectionsOfferingCourse.computeIfAbsent(sc.getSession().getSection().getName(), k -> new ArrayList<>()).add(sc);
                    }
                }

                String bestSection = null;

                for (Map.Entry<String, List<ScheduledClass>> entry : sectionsOfferingCourse.entrySet()) {
                    List<ScheduledClass> courseClasses = entry.getValue();
                    boolean hasClash = false;
                    
                    for (ScheduledClass coreClass : studentSchedule) {
                        for (ScheduledClass repeatClass : courseClasses) {
                            if (coreClass.getTimeslot().equals(repeatClass.getTimeslot())) {
                                hasClash = true;
                                break;
                            }
                        }
                        if (hasClash) break;
                    }

                    if (!hasClash) {
                        bestSection = entry.getKey();
                        break; 
                    }
                }
                if (bestSection != null) {
                    studentSchedule.addAll(sectionsOfferingCourse.get(bestSection));
                } else {
                    System.err.println("Warning: Could not schedule " + courseToRepeat + " for repeater " + student.getStudentName() + ".");
                }
            }

            timetable.addRepeaterSchedule(student, studentSchedule);
        }

    }

    private class ConflictReporter {
        private Map<String, Integer> conflictCounts = new HashMap<>();

        public void log(String reason) {
            conflictCounts.put(reason, conflictCounts.getOrDefault(reason, 0) + 1);
        }

        public String getTopConflictsReport() {
            if (conflictCounts.isEmpty()) return "No specific conflicts detected.";
            
            return conflictCounts.entrySet().stream()
                    .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                    .limit(5)
                    .map(e -> "- " + e.getKey() + " (" + e.getValue() + " times)")
                    .collect(Collectors.joining("\n"));
        }
    }
}
