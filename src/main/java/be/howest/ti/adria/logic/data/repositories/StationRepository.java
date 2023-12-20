package be.howest.ti.adria.logic.data.repositories;

import be.howest.ti.adria.logic.domain.observables.Station;

import java.util.List;

public interface StationRepository {
    List<Station> getStations();
    Station getStation(int id);

    Station insertStation(String name, double latitude, double longitude);
    Station updateStation(int id, String name, double latitude, double longitude);
    void deleteStation(int id);
}
