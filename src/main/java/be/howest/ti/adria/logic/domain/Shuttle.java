package be.howest.ti.adria.logic.domain;

public class Shuttle extends PureObservable {
    private final int id;
    private final String serial;


    public Shuttle(int id, String serial) {
        this.id = id;
        this.serial = serial;
    }


    public String getSerial() {
        return serial;
    }


    @Override
    public int getId() {
        return id;
    }
}
