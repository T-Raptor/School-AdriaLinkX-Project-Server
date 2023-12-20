package be.howest.ti.adria.logic.data.repositories;

import be.howest.ti.adria.logic.domain.observables.Reservation;
import be.howest.ti.adria.logic.domain.observables.Track;

import java.sql.Timestamp;
import java.util.List;

public interface ReservationRepository {
    List<Reservation> getReservations();
    Reservation getReservation(int id);

    Reservation insertReservation(Timestamp periodStart, Timestamp periodStop, String company, List<Track> route);
    void deleteReservation(int id);
}