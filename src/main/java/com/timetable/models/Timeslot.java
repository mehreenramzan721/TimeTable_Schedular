package com.timetable.models;

import java.util.Objects;

public class Timeslot implements Comparable<Timeslot> {
    private String dayOfWeek;
    private String startTime;
    private String endTime;

    public Timeslot(String dayOfWeek, String startTime, String endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timeslot timeslot = (Timeslot) o;
        return Objects.equals(dayOfWeek, timeslot.dayOfWeek) &&
               Objects.equals(startTime, timeslot.startTime) &&
               Objects.equals(endTime, timeslot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, startTime, endTime);
    }

    @Override
    public String toString() {
        return dayOfWeek + " " + startTime + " - " + endTime;
    }

    @Override
    public int compareTo(Timeslot o) {
        int dayCmp = this.dayOfWeek.compareTo(o.dayOfWeek);
        if (dayCmp != 0) return dayCmp;
        return this.startTime.compareTo(o.startTime);
    }
}
