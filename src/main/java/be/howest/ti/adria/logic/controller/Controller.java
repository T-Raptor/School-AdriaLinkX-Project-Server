package be.howest.ti.adria.logic.controller;

import be.howest.ti.adria.logic.domain.*;

import java.util.List;

public interface Controller {
    List<Station> getStations();
    List<Track> getTracks();
    List<Shuttle> getShuttles();

    List<Reservation> getReservations();
    Reservation placeReservation(ReservationProposal proposal);

    List<Event> searchEvents(EventFilter filter);
    Event pushEvent(EventProposal proposal);
    List<Notification> popUnreadNotifications(String company);
}
