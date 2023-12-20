package be.howest.ti.adria.logic.domain.proposals;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonCreator
    public EventProposal(
            @JsonProperty("target") int target,
            @JsonProperty("moment") Timestamp moment,
            @JsonProperty("subject") String subject,
            @JsonProperty("reason") String reason
    ) {
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
