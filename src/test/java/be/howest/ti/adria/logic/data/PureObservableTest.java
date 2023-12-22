package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.domain.PureObservable;
import be.howest.ti.adria.logic.domain.observables.Shuttle;
import be.howest.ti.adria.logic.domain.observables.Station;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class PureObservableTest {
    @Test
    void equalsOnId() {
        // Arrange
        PureObservable o1 = new Station(5, "fjfkdflj", 45, 23);
        PureObservable o2 = new Station(5, "azerdkfk", 11, 33);

        // Act
        boolean result = Objects.equals(o1, o2);

        // Assert
        assertTrue(result);
    }

    @Test
    void equalsIgnoresClass() {
        // Arrange
        PureObservable o1 = new Station(5, "fjfkdflj", 45, 23);
        PureObservable o2 = new Shuttle(5, "MOROC-AA55-22");

        // Act
        boolean result = Objects.equals(o1, o2);

        // Assert
        assertTrue(result);
    }

    @Test
    void equalsNotNull() {
        // Arrange
        PureObservable o1 = new Shuttle(5, "ooo-55-4dd");
        PureObservable o2 = null;

        // Act
        boolean result = Objects.equals(o1, o2);

        // Assert
        assertFalse(result);
    }

    @Test
    void hashCodeTest() {
        // Arrange
        int id = 5;
        int expectedHash = Objects.hash(id);
        PureObservable o1 = new Shuttle(id, "azer-f55f-aaz");

        // Act
        int actualHash = Objects.hashCode(o1);

        // Assert
        assertEquals(expectedHash, actualHash);
    }
}
