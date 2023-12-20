package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.domain.observables.Shuttle;
import io.netty.util.internal.StringUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public abstract class ShuttleRepositoryTest {
    protected ShuttleRepository repository;


    @Test
    void getShuttles() {
        // Arrange

        // Act
        List<Shuttle> shuttles = repository.getShuttles();

        // Assert
        Assertions.assertNotNull(shuttles);
        Assertions.assertFalse(shuttles.isEmpty());
    }

    @Test
    void getShuttle() {
        // Arrange
        int id = 9;

        // Act
        Shuttle shuttle = repository.getShuttle(id);

        // Assert
        Assertions.assertNotNull(shuttle);
        Assertions.assertFalse(StringUtil.isNullOrEmpty(shuttle.getSerial()));
    }

    @Test
    void insertShuttle() {
        // Arrange
        String serial = "AAAA-BBBB-CCCC";

        // Act
        Shuttle shuttle = repository.insertShuttle(serial);

        // Assert
        Assertions.assertNotNull(shuttle);
        Assertions.assertEquals(serial, shuttle.getSerial());
    }
}
