package com.timetable.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Batch {
    private String id;
    private String name;
    private List<Section> sections;
    private List<Subject> subjects;

    public Batch(String id, String name) {
        this.id = id;
        this.name = name;
        this.sections = new ArrayList<>();
        this.subjects = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Section> getSections() { return sections; }
    public void setSections(List<Section> sections) { this.sections = sections; }

    public List<Subject> getSubjects() { return subjects; }
    public void setSubjects(List<Subject> subjects) { this.subjects = subjects; }

    public void addSection(Section section) {
        this.sections.add(section);
        section.setBatch(this);
    }

    public void addSubject(Subject subject) {
        this.subjects.add(subject);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Batch batch = (Batch) o;
        return Objects.equals(id, batch.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
