package be.howest.ti.adria.logic.domain.proposals;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class LocalEventProposal extends EventProposal {
    private final double latitude;
    private final double longitude;


    public LocalEventProposal(int target, Timestamp moment, String subject, double latitude, double longitude) {
        super(target, moment, subject);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @JsonCreator
    public LocalEventProposal(
            @JsonProperty("target") int target,
            @JsonProperty("moment") Timestamp moment,
            @JsonProperty("subject") String subject,
            @JsonProperty("latitude") double latitude,
            @JsonProperty("longitude") double longitude,
            @JsonProperty("reason") String reason
    ) {
        super(target, moment, subject, reason);
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
