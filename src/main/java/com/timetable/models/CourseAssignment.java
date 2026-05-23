package com.timetable.models;

public class CourseAssignment {
    private String courseName;
    private String teacherName;
    private String labTeacherName;
    private String batchName;
    private String sectionName;
    private int creditHours;
    private boolean requiresLab;

    public CourseAssignment(String courseName, String teacherName, String labTeacherName, String batchName, String sectionName, int creditHours, boolean requiresLab) {
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.labTeacherName = labTeacherName;
        this.batchName = batchName;
        this.sectionName = sectionName;
        this.creditHours = creditHours;
        this.requiresLab = requiresLab;
    }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getLabTeacherName() { return labTeacherName; }
    public void setLabTeacherName(String labTeacherName) { this.labTeacherName = labTeacherName; }

    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }

    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }

    public int getCreditHours() { return creditHours; }
    public void setCreditHours(int creditHours) { this.creditHours = creditHours; }

    public boolean isRequiresLab() { return requiresLab; }
    public void setRequiresLab(boolean requiresLab) { this.requiresLab = requiresLab; }
}
