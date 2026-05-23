package com.timetable.models;

import java.util.ArrayList;
import java.util.List;

public class RepeaterStudent {
    private String studentName;
    private String originalBatch;
    private String originalSection;
    private List<String> coursesToRepeat;

    public RepeaterStudent(String studentName, String originalBatch, String originalSection) {
        this.studentName = studentName;
        this.originalBatch = originalBatch;
        this.originalSection = originalSection;
        this.coursesToRepeat = new ArrayList<>();
    }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getOriginalBatch() { return originalBatch; }
    public void setOriginalBatch(String originalBatch) { this.originalBatch = originalBatch; }

    public String getOriginalSection() { return originalSection; }
    public void setOriginalSection(String originalSection) { this.originalSection = originalSection; }

    public List<String> getCoursesToRepeat() { return coursesToRepeat; }
    public void setCoursesToRepeat(List<String> coursesToRepeat) { this.coursesToRepeat = coursesToRepeat; }
}
