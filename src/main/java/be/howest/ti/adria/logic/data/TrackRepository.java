package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.domain.observables.Station;
import be.howest.ti.adria.logic.domain.observables.Track;

import java.util.List;

public interface TrackRepository {
    List<Track> getTracks();
    Track getTrack(int id);

    Track insertTrack(Station station1, Station station2);
    Track updateTrack(int id, Station station1, Station station2);
    void deleteTrack(int id);
}
