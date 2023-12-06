package be.howest.ti.adria.logic.domain;

public class Track implements Observable {
    private final int id;
    private final String company;
    private final Station station1;
    private final Station station2;


    public Track(int id, String company, Station station1, Station station2) {
        this.id = id;
        this.company = company;
        this.station1 = station1;
        this.station2 = station2;
    }


    public String getCompany() {
        return this.company;
    }

    public Station getStation1() {
        return this.station1;
    }

    public Station getStation2() {
        return this.station2;
    }


    @Override
    public int getId() {
        return this.id;
    }
}
