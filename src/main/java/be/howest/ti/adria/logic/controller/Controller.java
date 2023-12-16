package be.howest.ti.adria.logic.controller;

import be.howest.ti.adria.logic.domain.Quote;
import be.howest.ti.adria.logic.domain.Reservation;
import be.howest.ti.adria.logic.domain.Station;
import be.howest.ti.adria.logic.domain.Track;

import java.sql.Timestamp;
import java.util.List;

public interface Controller {
    Quote getQuote(int quoteId);

    Quote createQuote(String quote);

    Quote updateQuote(int quoteId, String quote);

    void deleteQuote(int quoteId);


    List<Station> getStations();

    List<Track> getReservations();

    List<Track> getTracks();

//    List<Track> getTracks();

    Reservation insertReservation(Timestamp periodStart, Timestamp periodStop, String company, List<Track> route);
}
