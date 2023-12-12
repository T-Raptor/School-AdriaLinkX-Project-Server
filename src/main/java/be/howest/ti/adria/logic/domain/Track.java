package be.howest.ti.adria.logic.domain;

public class Track implements Observable {
    private final int id;
    private final Station station1;
    private final Station station2;


    public Track(int id, Station station1, Station station2) {
        this.id = id;
        this.station1 = station1;
        this.station2 = station2;
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
