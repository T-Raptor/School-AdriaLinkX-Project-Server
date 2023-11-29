package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.domain.Station;

public interface StationRepository {
    Station getStations();
    Station getStation(int id);

    Station insertStation(String name, double latitude, double longitude);
    Station deleteStation(int id);

    Station updateStation(int id, String name, double latitude, double longitude);
    Station updateStationName(int id, String name);
    Station updateStationLatitude(int id, double latitude);
    Station updateStationLongitude(int id, double longitude);
}
