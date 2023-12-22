package be.howest.ti.adria.logic.domain;

import java.sql.Timestamp;

public class EventFilter {
    private Integer target;
    private String subject;
    private Timestamp earliest;
    private Timestamp latest;


    public EventFilter() {
        this.target = null;
        this.subject = null;
        this.earliest = null;
        this.latest = null;
    }


    public Integer getTarget() {
        return target;
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


    public void setTarget(Integer target) {
        this.target = target;
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
