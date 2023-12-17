package be.howest.ti.adria.logic.domain;

import java.sql.Timestamp;

public class LocalEventProposal extends EventProposal {
    private final double latitude;
    private final double longitude;


    public LocalEventProposal(int target, Timestamp moment, String subject, double latitude, double longitude) {
        super(target, moment, subject);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocalEventProposal(int target, Timestamp moment, String subject, double latitude, double longitude, String reason) {
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
