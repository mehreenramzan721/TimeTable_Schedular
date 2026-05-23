package com.timetable.models;

public class ScheduledClass {
    private Session session;
    private Timeslot timeslot;
    private Room room;

    public ScheduledClass(Session session, Timeslot timeslot, Room room) {
        this.session = session;
        this.timeslot = timeslot;
        this.room = room;
    }

    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    public Timeslot getTimeslot() { return timeslot; }
    public void setTimeslot(Timeslot timeslot) { this.timeslot = timeslot; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    @Override
    public String toString() {
        return session.toString() + " at " + timeslot.toString() + " in " + room.getName();
    }
}
