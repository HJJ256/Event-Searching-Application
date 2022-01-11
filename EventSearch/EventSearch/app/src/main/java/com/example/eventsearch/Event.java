package com.example.eventsearch;

import java.util.Objects;

public class Event {
    String event,id,date,category,venue;

    public Event(String id,String event,String date,String category,String venue){
        this.id = id;
        this.event = event;
        this.date = date;
        this.category = category;
        this.venue = venue;
    }

    public Event(){
        this.id = "";
        this.event = "N/A";
        this.date = "N/A";
        this.category = "N/A";
        this.venue = "N/A";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event1 = (Event) o;
        return Objects.equals(id, event1.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, id, date, category, venue);
    }
}
