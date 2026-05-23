package com.timetable.models;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Timetable {
    private List<ScheduledClass> classes;
    private Map<RepeaterStudent, List<ScheduledClass>> repeaterSchedules;

    public Timetable() {
        this.classes = new LinkedList<>();
        this.repeaterSchedules = new HashMap<>();
    }

    public List<ScheduledClass> getClasses() {
        return classes;
    }

    public void addClass(ScheduledClass sc) {
        this.classes.add(sc);
    }

    public List<ScheduledClass> getClassesForSection(Section section) {
        return classes.stream()
                .filter(c -> c.getSession().getSection().equals(section))
                .collect(Collectors.toList());
    }

    public List<ScheduledClass> getClassesForTeacher(Teacher teacher) {
        return classes.stream()
                .filter(c -> c.getSession().getTeacher().equals(teacher))
                .collect(Collectors.toList());
    }

    public List<ScheduledClass> getClassesForRoom(Room room) {
        return classes.stream()
                .filter(c -> c.getRoom().equals(room))
                .collect(Collectors.toList());
    }

    public Map<RepeaterStudent, List<ScheduledClass>> getRepeaterSchedules() {
        return repeaterSchedules;
    }

    public void addRepeaterSchedule(RepeaterStudent student, List<ScheduledClass> scList) {
        repeaterSchedules.put(student, scList);
    }
}
