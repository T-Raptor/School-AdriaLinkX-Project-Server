package be.howest.ti.adria.logic.controller;

import be.howest.ti.adria.logic.domain.Quote;
import be.howest.ti.adria.logic.domain.Reservation;
import be.howest.ti.adria.logic.domain.Station;
import be.howest.ti.adria.logic.domain.Track;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MockController implements Controller {
    private static final String SOME_QUOTE = "quote";
    @Override
    public Quote getQuote(int quoteId) {
        return new Quote(quoteId, SOME_QUOTE);
    }

    @Override
    public Quote createQuote(String quote) {
        return new Quote(1, quote);
    }

    @Override
    public Quote updateQuote(int quoteId, String quote) {
        return new Quote(quoteId, quote);
    }

    @Override
    public void deleteQuote(int quoteId) {
    }


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
    public List<Reservation> getReservations() {
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(new Reservation(7, Timestamp.valueOf("2022-05-08 14:30:00"), Timestamp.valueOf("2022-05-08 18:30:00"), "Hoogle", getTracks()));
        reservations.add(new Reservation(8, Timestamp.valueOf("2022-05-12 9:30:00"), Timestamp.valueOf("2022-05-09 11:30:00"), "Macrosoft", getTracks()));
        return reservations;
    }
}
