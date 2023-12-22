package be.howest.ti.adria.logic.data.repositories;

import be.howest.ti.adria.logic.domain.observables.Reservation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;

public abstract class ReservationRepositoryTest {
    protected TrackRepository trackRepository;
    protected ReservationRepository repository;

    @Test
    void getReservations() {
        // Arrange

        // Act
        List<Reservation> reservations = repository.getReservations();

        // Assert
        Assertions.assertNotNull(reservations);
        Assertions.assertFalse(reservations.isEmpty());
    }

    @Test
    void getReservation() {
        // Arrange
        int id = 7;

        // Act
        Reservation reservation = repository.getReservation(id);

        // Assert
        Assertions.assertNotNull(reservation);
        Assertions.assertNotNull(reservation.getPeriodStart());
        Assertions.assertNotNull(reservation.getPeriodStop());
        Assertions.assertNotNull(reservation.getCompany());
        Assertions.assertNotNull(reservation.getRoute());
    }

    @Test
    void insertReservation() {
        // Arrange
        String company = "Macrosoft";
        Timestamp periodStart = Timestamp.valueOf("2024-09-12 20:00:00");
        Timestamp periodStop = Timestamp.valueOf("2024-09-12 23:00:00");
        List<Integer> route = List.of(4, 5);

        // Act
        Reservation reservation = repository.insertReservation(periodStart, periodStop, company, route);

        // Assert
        Assertions.assertNotNull(reservation);
        Assertions.assertEquals(company, reservation.getCompany());
        Assertions.assertEquals(periodStart, reservation.getPeriodStart());
        Assertions.assertEquals(periodStop, reservation.getPeriodStop());

        Assertions.assertEquals(route.size(), reservation.getRoute().size());
        Assertions.assertTrue(reservation.getRoute().stream().allMatch(track -> route.contains(track.getId())));
    }

    @Test
    void deleteReservation() {
        // Arrange
        String company = "Macrosoft";
        Timestamp periodStart = Timestamp.valueOf("2024-09-12 20:00:00");
        Timestamp periodStop = Timestamp.valueOf("2024-09-12 23:00:00");
        List<Integer> route = List.of(4, 5);
        Reservation reservation = repository.insertReservation(periodStart, periodStop, company, route);

        // Act
        repository.deleteReservation(reservation.getId());

        // Assert
        Assertions.assertNull(repository.getReservation(reservation.getId()));
    }
}
