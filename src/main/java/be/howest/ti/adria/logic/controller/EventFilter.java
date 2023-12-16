package be.howest.ti.adria.logic.controller;

import be.howest.ti.adria.logic.domain.Observable;

import java.sql.Timestamp;

public class EventFilter {
    private Observable observable;
    private String subject;
    private Timestamp earliest;
    private Timestamp latest;


    public EventFilter() {
        this.observable = null;
        this.subject = null;
    }


    public Observable getObservable() {
        return observable;
    }

    public String getSubject() {
        return subject;
    }

    public Timestamp getEarliest() {
        return earliest;
    }

    public Timestamp getLatest() {
        return latest;
    }


    public void setObservable(Observable observable) {
        this.observable = observable;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setEarliest(Timestamp earliest) {
        this.earliest = earliest;
    }

    public void setLatest(Timestamp latest) {
        this.latest = latest;
    }
}
