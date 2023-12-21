package be.howest.ti.adria.logic.controller;

import be.howest.ti.adria.logic.domain.*;
import be.howest.ti.adria.logic.domain.observables.Reservation;
import be.howest.ti.adria.logic.domain.observables.Shuttle;
import be.howest.ti.adria.logic.domain.observables.Station;
import be.howest.ti.adria.logic.domain.observables.Track;
import be.howest.ti.adria.logic.domain.proposals.EventFilter;
import be.howest.ti.adria.logic.domain.proposals.EventProposal;
import be.howest.ti.adria.logic.domain.proposals.ReservationProposal;
import be.howest.ti.adria.logic.domain.proposals.ShuttleProposal;

import java.util.List;

public interface Controller {
    List<Station> getStations();
    List<Track> getTracks();

    List<Shuttle> getShuttles();
    Shuttle registerShuttle(ShuttleProposal proposal);

    List<Reservation> getReservations();
    Reservation placeReservation(ReservationProposal proposal);

    List<Event> searchEvents(EventFilter filter);
    Event pushEvent(EventProposal proposal);
    List<Notification> popUnreadNotifications(String company);
}
