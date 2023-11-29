package be.howest.ti.adria.logic.domain;

public class Station implements Observable {
    private final int id;
    private final String name;
    private final double latitude;
    private final double longitude;


    public Station(int id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getName() {
        return this.name;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public int getId() {
        return this.id;
    }
}
