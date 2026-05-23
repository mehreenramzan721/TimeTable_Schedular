package com.timetable.models;

public class Session {
    private Subject subject;
    private Teacher teacher;
    private Section section;
    private boolean isLab;
    private double durationHours;
    private int consecutiveSlots;

    public Session(Subject subject, Teacher teacher, Section section, boolean isLab, double durationHours, int consecutiveSlots) {
        this.subject = subject;
        this.teacher = teacher;
        this.section = section;
        this.isLab = isLab;
        this.durationHours = durationHours;
        this.consecutiveSlots = consecutiveSlots;
    }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public Section getSection() { return section; }
    public void setSection(Section section) { this.section = section; }

    public boolean isLab() { return isLab; }
    public void setLab(boolean lab) { isLab = lab; }

    public double getDurationHours() { return durationHours; }
    public void setDurationHours(double durationHours) { this.durationHours = durationHours; }

    public int getConsecutiveSlots() { return consecutiveSlots; }
    public void setConsecutiveSlots(int consecutiveSlots) { this.consecutiveSlots = consecutiveSlots; }

    @Override
    public String toString() {
        return subject.getName() + (isLab ? " (Lab)" : "") + " by " + teacher.getName() + " for " + section.getName();
    }
}
