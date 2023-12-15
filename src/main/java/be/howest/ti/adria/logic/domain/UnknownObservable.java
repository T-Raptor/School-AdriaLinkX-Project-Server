package be.howest.ti.adria.logic.domain;

public class UnknownObservable implements Observable {
    private final int id;


    public UnknownObservable(int id) {
        this.id = id;
    }


    @Override
    public int getId() {
        return id;
    }
}
