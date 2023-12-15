package be.howest.ti.adria.logic.domain;

import java.sql.Timestamp;

public class LocalEvent extends Event {
    private final double latitude;
    private final double longitude;


    public LocalEvent(int id, Observable target, Timestamp moment, String subject, double latitude, double longitude) {
        super(id, target, moment, subject);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocalEvent(int id, Observable target, Timestamp moment, String subject, double latitude, double longitude, String reason) {
        super(id, target, moment, subject, reason);
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
