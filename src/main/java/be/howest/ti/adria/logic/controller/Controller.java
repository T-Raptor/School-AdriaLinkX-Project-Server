package be.howest.ti.adria.logic.controller;

import be.howest.ti.adria.logic.domain.*;

import java.util.List;

public interface Controller {
    Quote getQuote(int quoteId);

    Quote createQuote(String quote);

    Quote updateQuote(int quoteId, String quote);

    void deleteQuote(int quoteId);


    List<Station> getStations();

    List<Track> getTracks();

    List<Event> searchEvents(EventFilter filter);
}
