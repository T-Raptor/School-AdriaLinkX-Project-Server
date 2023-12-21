package be.howest.ti.adria.logic.data.repositories;

import be.howest.ti.adria.logic.domain.observables.Station;
import io.netty.util.internal.StringUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public abstract class StationRepositoryTest {
    protected StationRepository repository;

    private static final double EPSILON = 0.01;

    @Test
    void getStations() {
        // Arrange

        // Act
        List<Station> stations = repository.getStations();

        // Assert
        Assertions.assertNotNull(stations);
        Assertions.assertEquals(3, stations.size());
    }

    @Test
    void getStation() {
        // Arrange
        int id = 1;

        // Act
        Station station = repository.getStation(id);

        // Assert
        Assertions.assertNotNull(station);
        Assertions.assertFalse(StringUtil.isNullOrEmpty(station.getName()));
    }

    @Test
    void updateStation() {
        // Arrange
        int id = 1;
        String name = "Whiterun";
        double latitude = 52.51758898267697;
        double longitude = -1.8196309251176122;

        // Act
        Station station = repository.updateStation(
                id,
                name,
                latitude,
                longitude
        );

        // Assert
        Assertions.assertNotNull(station);
        Assertions.assertEquals(name, station.getName());
        Assertions.assertEquals(latitude, station.getLatitude(), EPSILON);
        Assertions.assertEquals(longitude, station.getLongitude(), EPSILON);
    }

    @Test
    void insertStation() {
        // Arrange
        String name = "Solitude";
        double latitude = 52.51758898267697;
        double longitude = -1.8196309251176122;

        // Act
        Station station = repository.insertStation(name, latitude, longitude);

        // Assert
        Assertions.assertNotNull(station);
        Assertions.assertEquals(name, station.getName());
        Assertions.assertEquals(latitude, station.getLatitude(), EPSILON);
        Assertions.assertEquals(longitude, station.getLongitude(), EPSILON);
    }

    @Test
    void deleteStation() {
        // Arrange
        Station station = repository.insertStation("Auriga", 0, 0);

        // Act
        repository.deleteStation(station.getId());

        // Assert
        Assertions.assertNull(repository.getStation(station.getId()));
    }
}
