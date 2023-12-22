package be.howest.ti.adria.logic.domain.observables;

import be.howest.ti.adria.logic.domain.Observable;

import java.sql.Timestamp;
import java.util.List;

public class Reservation implements Observable {
    private final int id;
    private final Timestamp periodStart;
    private final Timestamp periodStop;
    private final String company;
    private final List<Track> route;


    public Reservation(int id, Timestamp periodStart, Timestamp periodStop, String company, List<Track> route) {
        this.id = id;
        this.periodStart = periodStart;
        this.periodStop = periodStop;
        this.company = company;
        this.route = route;
    }


    public Timestamp getPeriodStart() {
        return periodStart;
    }

    public Timestamp getPeriodStop() {
        return periodStop;
    }

    public String getCompany() {
        return company;
    }

    public List<Track> getRoute() {
        return route;
    }


    @Override
    public int getId() {
        return id;
    }
}
