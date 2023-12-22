package be.howest.ti.adria.logic.data.repositories;

import be.howest.ti.adria.logic.domain.observables.Reservation;

import java.sql.Timestamp;
import java.util.List;

public interface ReservationRepository {
    List<Reservation> getReservations();
    Reservation getReservation(int id);

    Reservation insertReservation(Timestamp periodStart, Timestamp periodStop, String company, List<Integer> route);
    void deleteReservation(int id);
}