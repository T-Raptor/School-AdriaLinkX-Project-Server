package be.howest.ti.adria.logic.data.repositories;

import be.howest.ti.adria.logic.domain.Notification;
import be.howest.ti.adria.logic.domain.Observable;
import be.howest.ti.adria.logic.domain.ObservableInfo;
import be.howest.ti.adria.logic.domain.observables.Reservation;
import be.howest.ti.adria.logic.domain.observables.Shuttle;
import be.howest.ti.adria.logic.domain.observables.Station;
import be.howest.ti.adria.logic.domain.observables.Track;
import be.howest.ti.adria.logic.exceptions.RepositoryException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public abstract class ObservableRepositoryTest {
    protected ObservableRepository repository;

    @Test
    void getObservableInfo() {
        // Arrange
        int id = 1;

        // Act
        ObservableInfo observableInfo = repository.getObservableInfo(id);

        // Assert
        Assertions.assertNotNull(observableInfo);
        Assertions.assertEquals(1, observableInfo.getId());
        Assertions.assertEquals("Station", observableInfo.getSubtype());
    }

    @Test
    void insertObservableInfo() {
        // Arrange
        Class<Reservation> subtype = Reservation.class;

        // Act
        ObservableInfo observableInfo = repository.insertObservableInfo(subtype);

        // Assert
        Assertions.assertNotNull(observableInfo);
        Assertions.assertEquals(subtype.getSimpleName(), observableInfo.getSubtype());
    }


    @Test
    void getObservableReservation() {
        // Arrange
        int id = 8;

        // Act
        Observable observable = repository.getObservable(id);

        // Assert
        Assertions.assertNotNull(observable);
        Assertions.assertTrue(observable instanceof Reservation);
    }

    @Test
    void getObservableShuttle() {
        // Arrange
        int id = 9;

        // Act
        Observable observable = repository.getObservable(id);

        // Assert
        Assertions.assertNotNull(observable);
        Assertions.assertTrue(observable instanceof Shuttle);
    }

    @Test
    void getObservableStation() {
        // Arrange
        int id = 1;

        // Act
        Observable observable = repository.getObservable(id);

        // Assert
        Assertions.assertNotNull(observable);
        Assertions.assertTrue(observable instanceof Station);
    }

    @Test
    void getObservableTrack() {
        // Arrange
        int id = 4;

        // Act
        Observable observable = repository.getObservable(id);

        // Assert
        Assertions.assertNotNull(observable);
        Assertions.assertTrue(observable instanceof Track);
    }

    @Test
    void getObservableUnknownThrows() {
        // Arrange
        int id = 275;

        // Act / Assert
        Assertions.assertThrows(RepositoryException.class, () -> repository.getObservable(id));
    }
}
