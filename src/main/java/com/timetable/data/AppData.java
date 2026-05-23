package com.timetable.data;

import com.timetable.models.Batch;
import com.timetable.models.Room;
import com.timetable.models.Subject;
import com.timetable.models.Teacher;
import com.timetable.models.CourseAssignment;

import com.timetable.models.RepeaterStudent;

import java.util.ArrayList;
import java.util.List;

public class AppData {
    private List<Teacher> teachers = new ArrayList<>();
    private List<Subject> subjects = new ArrayList<>();
    private List<Room> rooms = new ArrayList<>();
    private List<Batch> batches = new ArrayList<>();
    private List<CourseAssignment> assignments = new ArrayList<>();
    private List<RepeaterStudent> repeaterStudents = new ArrayList<>();

    public List<Teacher> getTeachers() { return teachers; }
    public void setTeachers(List<Teacher> teachers) { this.teachers = teachers; }

    public List<Subject> getSubjects() { return subjects; }
    public void setSubjects(List<Subject> subjects) { this.subjects = subjects; }

    public List<Room> getRooms() { return rooms; }
    public void setRooms(List<Room> rooms) { this.rooms = rooms; }

    public List<Batch> getBatches() { return batches; }
    public void setBatches(List<Batch> batches) { this.batches = batches; }

    public List<CourseAssignment> getAssignments() { return assignments; }
    public void setAssignments(List<CourseAssignment> assignments) { this.assignments = assignments; }

    public List<RepeaterStudent> getRepeaterStudents() { return repeaterStudents; }
    public void setRepeaterStudents(List<RepeaterStudent> repeaterStudents) { this.repeaterStudents = repeaterStudents; }
}
