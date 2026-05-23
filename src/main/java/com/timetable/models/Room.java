package com.timetable.models;

import java.util.Objects;

public class Room {
    private String id;
    private String name;
    private boolean isLab;

    public Room(String id, String name, boolean isLab) {
        this.id = id;
        this.name = name;
        this.isLab = isLab;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isLab() { return isLab; }
    public void setLab(boolean lab) { isLab = lab; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + (isLab ? " (Lab)" : "");
    }
}
