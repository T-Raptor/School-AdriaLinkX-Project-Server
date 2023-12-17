package be.howest.ti.adria.logic.domain;

import java.sql.Timestamp;

public class EventProposal {
    private final int target;
    private final Timestamp moment;
    private final String subject;
    private final String reason;


    public EventProposal(int target, Timestamp moment, String subject) {
        this.target = target;
        this.moment = moment;
        this.subject = subject;
        this.reason = null;
    }

    public EventProposal(int target, Timestamp moment, String subject, String reason) {
        this.target = target;
        this.moment = moment;
        this.subject = subject;
        this.reason = reason;
    }


    public int getTarget() {
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
