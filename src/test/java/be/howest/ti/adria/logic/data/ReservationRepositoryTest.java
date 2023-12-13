package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.domain.Reservation;
import be.howest.ti.adria.logic.domain.Station;
import be.howest.ti.adria.logic.domain.Reservation;
import be.howest.ti.adria.logic.domain.Track;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
    void updateReservation() {
        // Arrange
        int id = 7;
        String company = "Macrosoft";
        Timestamp periodStart = new Timestamp(2024, 9, 12, 20, 0, 0, 0);
        Timestamp periodStop = new Timestamp(2024, 9, 12, 23, 0, 0, 0);
        List<Track> route = trackRepository.getTracks();

        // Act
        Reservation reservation = repository.updateReservation(
                id,
                periodStart,
                periodStop,
                company,
                route
        );

        // Assert
        Assertions.assertNotNull(reservation);
        Assertions.assertEquals(company, reservation.getCompany());
        Assertions.assertEquals(periodStart, reservation.getPeriodStart());
        Assertions.assertEquals(periodStop, reservation.getPeriodStop());
        Assertions.assertEquals(route, reservation.getRoute());
    }

    @Test
    void insertReservation() {
        // Arrange
        String company = "Macrosoft";
        Timestamp periodStart = new Timestamp(2024, 9, 12, 20, 0, 0, 0);
        Timestamp periodStop = new Timestamp(2024, 9, 12, 23, 0, 0, 0);
        List<Track> route = trackRepository.getTracks();

        // Act
        Reservation reservation = repository.insertReservation(periodStart, periodStop, company, route);

        // Assert
        Assertions.assertNotNull(reservation);
        Assertions.assertEquals(company, reservation.getCompany());
        Assertions.assertEquals(periodStart, reservation.getPeriodStart());
        Assertions.assertEquals(periodStop, reservation.getPeriodStop());
        Assertions.assertEquals(route, reservation.getRoute());
    }

    @Test
    void deleteReservation() {
        // Arrange
        String company = "Macrosoft";
        Timestamp periodStart = new Timestamp(2024, 9, 12, 20, 0, 0, 0);
        Timestamp periodStop = new Timestamp(2024, 9, 12, 23, 0, 0, 0);
        List<Track> route = trackRepository.getTracks();
        Reservation reservation = repository.insertReservation(periodStart, periodStop, company, route);

        // Act
        repository.deleteReservation(reservation.getId());

        // Assert
        Assertions.assertNull(repository.getReservation(reservation.getId()));
    }
}
