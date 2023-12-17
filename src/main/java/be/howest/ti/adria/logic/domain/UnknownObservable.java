package be.howest.ti.adria.logic.domain;

public class UnknownObservable extends PureObservable {
    private final int id;


    public UnknownObservable(int id) {
        this.id = id;
    }


    @Override
    public int getId() {
        return id;
    }
}
