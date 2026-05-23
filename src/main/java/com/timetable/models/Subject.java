package com.timetable.models;

import java.util.Objects;

public class Subject {
    private String id;
    private String name;
    private int creditHours;
    private boolean requiresLab;

    public Subject(String id, String name, int creditHours, boolean requiresLab) {
        this.id = id;
        this.name = name;
        this.creditHours = creditHours;
        this.requiresLab = requiresLab;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCreditHours() { return creditHours; }
    public void setCreditHours(int creditHours) { this.creditHours = creditHours; }

    public boolean isRequiresLab() { return requiresLab; }
    public void setRequiresLab(boolean requiresLab) { this.requiresLab = requiresLab; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return Objects.equals(id, subject.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + creditHours + " CR" + (requiresLab ? ", Lab" : "") + ")";
    }
}
