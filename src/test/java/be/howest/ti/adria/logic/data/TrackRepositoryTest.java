package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.domain.observables.Station;
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

    @Test
    void updateTrack() {
        // Arrange
        int id = 4;
        String company = "Macrosoft";
        Station station1 = stationRepository.getStation(3);
        Station station2 = stationRepository.getStation(1);

        // Act
        Track track = repository.updateTrack(
                id,
                station1,
                station2
        );

        // Assert
        Assertions.assertNotNull(track);
        Assertions.assertEquals(station1, track.getStation1());
        Assertions.assertEquals(station2, track.getStation2());
    }

    @Test
    void insertTrack() {
        // Arrange
        String company = "Macrosoft";
        Station station1 = stationRepository.getStation(3);
        Station station2 = stationRepository.getStation(1);

        // Act
        Track track = repository.insertTrack(station1, station2);

        // Assert
        Assertions.assertNotNull(track);
        Assertions.assertEquals(station1, track.getStation1());
        Assertions.assertEquals(station2, track.getStation2());
    }

    @Test
    void deleteTrack() {
        // Arrange
        Station station1 = stationRepository.getStation(3);
        Station station2 = stationRepository.getStation(1);
        Track track = repository.insertTrack(station1, station2);

        // Act
        repository.deleteTrack(track.getId());

        // Assert
        Assertions.assertNull(repository.getTrack(track.getId()));
    }
}
