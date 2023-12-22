package be.howest.ti.adria.logic.data.repositories;

import be.howest.ti.adria.logic.domain.Observable;
import be.howest.ti.adria.logic.domain.ObservableInfo;

public interface ObservableRepository {
    <T extends Observable> ObservableInfo insertObservableInfo(Class<T> subtype);
    ObservableInfo getObservableInfo(int id);
    Observable getObservable(int id);
}
