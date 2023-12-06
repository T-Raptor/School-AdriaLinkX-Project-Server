package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.domain.Station;
import be.howest.ti.adria.logic.domain.Track;

import java.util.List;

public interface TrackRepository {
    List<Track> getTracks();
    Track getTrack(int id);

    Track insertTrack(String company, Station station1, Station station2);
    Track updateTrack(int id, String company, Station station1, Station station2);
    void deleteTrack(int id);
}
