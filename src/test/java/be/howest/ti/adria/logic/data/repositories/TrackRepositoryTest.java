package be.howest.ti.adria.logic.data.repositories;

import be.howest.ti.adria.logic.domain.observables.Track;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public abstract class TrackRepositoryTest {
    protected StationRepository stationRepository;
    protected TrackRepository repository;

    @Test
    void getTracks() {
        // Arrange

        // Act
        List<Track> tracks = repository.getTracks();

        // Assert
        Assertions.assertNotNull(tracks);
        Assertions.assertFalse(tracks.isEmpty());
    }

    @Test
    void getTrack() {
        // Arrange
        int id = 4;

        // Act
        Track track = repository.getTrack(id);

        // Assert
        Assertions.assertNotNull(track);
        Assertions.assertNotNull(track.getStation1());
        Assertions.assertNotNull(track.getStation2());
    }
}
