package be.howest.ti.adria.logic.data.repositories;

import be.howest.ti.adria.logic.domain.observables.Station;

import java.util.List;

public interface StationRepository {
    List<Station> getStations();
    Station getStation(int id);
}
