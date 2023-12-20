package be.howest.ti.adria.logic.domain.observables;

import be.howest.ti.adria.logic.domain.PureObservable;

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
