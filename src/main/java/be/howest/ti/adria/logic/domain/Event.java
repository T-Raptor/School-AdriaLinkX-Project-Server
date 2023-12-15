package be.howest.ti.adria.logic.domain;

import java.sql.Timestamp;

public class Event {
    private final int id;
    private final Observable target;
    private final Timestamp moment;
    private final String subject;
    private final String reason;


    public Event(int id, Observable target, Timestamp moment, String subject) {
        this.id = id;
        this.target = target;
        this.moment = moment;
        this.subject = subject;
        this.reason = null;
    }

    public Event(int id, Observable target, Timestamp moment, String subject, String reason) {
        this.id = id;
        this.target = target;
        this.moment = moment;
        this.subject = subject;
        this.reason = reason;
    }


    public int getId() {
        return id;
    }

    public Observable getTarget() {
        return target;
    }

    public Timestamp getMoment() {
        return moment;
    }

    public String getSubject() {
        return subject;
    }

    public String getReason() {
        return reason;
    }
}
