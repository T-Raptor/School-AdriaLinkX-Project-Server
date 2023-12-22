package be.howest.ti.adria.logic.data.repositories;

import be.howest.ti.adria.logic.domain.observables.Track;

import java.util.List;

public interface TrackRepository {
    List<Track> getTracks();
    Track getTrack(int id);
}
