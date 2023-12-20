package be.howest.ti.adria.logic.controller;

import be.howest.ti.adria.logic.domain.*;
import be.howest.ti.adria.logic.domain.proposals.EventProposal;
import be.howest.ti.adria.logic.domain.proposals.LocalEventProposal;
import be.howest.ti.adria.logic.domain.proposals.ReservationProposal;
import be.howest.ti.adria.logic.domain.proposals.ShuttleProposal;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MockController implements Controller {

    @Override
    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(new Station(1, "Adria", 50.85292760248162, 4.351725442466426));
        stations.add(new Station(1, "Bdria", 47.03051037331985, 2.286659149568905));
        return stations;
    }

    @Override
    public List<Track> getTracks() {
        List<Track> tracks = new ArrayList<>();
        tracks.add(new Track(
                3,
                new Station(1, "Adria", 50.85292760248162, 4.351725442466426),
                new Station(1, "Bdria", 47.03051037331985, 2.286659149568905))
        );
        return tracks;
    }

    @Override
    public List<Shuttle> getShuttles() {
        return List.of(
                new Shuttle(1, "AAAA-BBBB-CCCC"),
                new Shuttle(2, "DDDD-1111-C2DC")
        );
    }

    @Override
    public Shuttle registerShuttle(ShuttleProposal proposal) {
        return new Shuttle(1, proposal.getSerial());
    }

    @Override
    public List<Reservation> getReservations() {
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(new Reservation(7, Timestamp.valueOf("2022-05-08 14:30:00"), Timestamp.valueOf("2022-05-08 18:30:00"), "Hoogle", getTracks()));
        reservations.add(new Reservation(8, Timestamp.valueOf("2022-05-12 9:30:00"), Timestamp.valueOf("2022-05-09 11:30:00"), "Macrosoft", getTracks()));
        return reservations;
    }

    @Override
    public Reservation placeReservation(ReservationProposal proposal) {
        List<Track> tracks = new ArrayList<>();
        for (int trackId : proposal.getRoute()) {
            tracks.add(
                    new Track(trackId,
                            new Station(1, "A", 0, 0),
                            new Station(2, "B", 0, 0)
                    )
            );
        }
        return new Reservation(245, proposal.getPeriodStart(), proposal.getPeriodStop(), proposal.getCompany(), tracks);
    }

    @Override
    public List<Event> searchEvents(EventFilter filter) {
        List<Event> events = new ArrayList<>();
        events.add(new Event(2, new UnknownObservable(1), new Timestamp(1000), "BREAK"));
        events.add(new Event(3, new UnknownObservable(3), new Timestamp(1500), "WARN"));
        events.add(new LocalEvent(4, new Shuttle(2, "AAAA-BBBB-CCCC"), new Timestamp(2000), "MOVE", 20, 10));
        return events
                .stream()
                .filter(e -> filter.getEarliest() == null || filter.getEarliest().before(e.getMoment()) || filter.getEarliest().equals(e.getMoment()) )
                .filter(e -> filter.getLatest() == null || filter.getLatest().after(e.getMoment()) || filter.getLatest().equals(e.getMoment()) )
                .filter(e -> filter.getTarget() == null || filter.getTarget().equals(e.getTarget()) )
                .filter(e -> filter.getSubject() == null || filter.getSubject().equals(e.getSubject()) )
                .toList();
    }

    @Override
    public Event pushEvent(EventProposal proposal) {
        Event event;
        if (proposal instanceof LocalEventProposal localProposal) {
            event = new LocalEvent(
                    1,
                    new UnknownObservable(localProposal.getTarget()),
                    proposal.getMoment(), proposal.getSubject(),
                    localProposal.getLatitude(),
                    localProposal.getLongitude(),
                    proposal.getReason()
            );
        } else {
            event = new Event(
                    1,
                    new UnknownObservable(proposal.getTarget()),
                    proposal.getMoment(),
                    proposal.getSubject(),
                    proposal.getReason()
            );
        }
        return event;

    }

    @Override
    public List<Notification> popUnreadNotifications(String company) {
        return List.of(
                new Notification(new Event(1, new UnknownObservable(1), new Timestamp(1000), "WARN"), company, false)
        );
    }
}
