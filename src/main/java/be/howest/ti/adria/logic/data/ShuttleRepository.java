package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.domain.observables.Shuttle;

import java.util.List;

public interface ShuttleRepository {
    List<Shuttle> getShuttles();
    Shuttle getShuttle(int id);

    Shuttle insertShuttle(String serial);
}
