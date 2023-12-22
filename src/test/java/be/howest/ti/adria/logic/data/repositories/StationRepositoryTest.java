package be.howest.ti.adria.logic.data.repositories;

import be.howest.ti.adria.logic.domain.observables.Station;
import io.netty.util.internal.StringUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public abstract class StationRepositoryTest {
    protected StationRepository repository;


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
}
