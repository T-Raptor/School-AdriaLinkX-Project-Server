package be.howest.ti.adria.logic.domain;

import java.sql.Timestamp;
import java.util.List;

public class ReservationProposal {
    private final Timestamp periodStart;
    private final Timestamp periodStop;
    private final String company;
    private final List<Integer> route;


    public ReservationProposal(Timestamp periodStart, Timestamp periodStop, String company, List<Integer> route) {
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

    public List<Integer> getRoute() {
        return route;
    }
}
