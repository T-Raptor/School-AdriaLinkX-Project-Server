package be.howest.ti.adria.logic.controller;

import be.howest.ti.adria.logic.data.Repositories;
import be.howest.ti.adria.logic.domain.Quote;
import be.howest.ti.adria.logic.domain.Reservation;
import be.howest.ti.adria.logic.domain.Station;
import be.howest.ti.adria.logic.domain.Track;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * DefaultAdriaController is the default implementation for the AdriaController interface.
 * The controller shouldn't even know that it is used in an API context..
 *
 * This class and all other classes in the logic-package (or future sub-packages)
 * should use 100% plain old Java Objects (POJOs). The use of Json, JsonObject or
 * Strings that contain encoded/json data should be avoided here.
 * Keep libraries and frameworks out of the logic packages as much as possible.
 * Do not be afraid to create your own Java classes if needed.
 */
public class DefaultController implements Controller {
    private static final String MSG_QUOTE_ID_UNKNOWN = "No quote with id: %d";

    @Override
    public Quote getQuote(int quoteId) {
        Quote quote = Repositories.getH2Repo().getQuote(quoteId);
        if (null == quote)
            throw new NoSuchElementException(String.format(MSG_QUOTE_ID_UNKNOWN, quoteId));

        return quote;
    }

    @Override
    public Quote createQuote(String quote) {
        if (StringUtils.isBlank(quote))
            throw new IllegalArgumentException("An empty quote is not allowed.");

        return Repositories.getH2Repo().insertQuote(quote);
    }

    @Override
    public Quote updateQuote(int quoteId, String quote) {
        if (StringUtils.isBlank(quote))
            throw new IllegalArgumentException("No quote provided!");

        if (quoteId < 0)
            throw new IllegalArgumentException("No valid quote ID provided");

        if (null == Repositories.getH2Repo().getQuote(quoteId))
            throw new NoSuchElementException(String.format(MSG_QUOTE_ID_UNKNOWN, quoteId));

        return Repositories.getH2Repo().updateQuote(quoteId, quote);
    }

    @Override
    public void deleteQuote(int quoteId) {
        if (null == Repositories.getH2Repo().getQuote(quoteId))
            throw new NoSuchElementException(String.format(MSG_QUOTE_ID_UNKNOWN, quoteId));

        Repositories.getH2Repo().deleteQuote(quoteId);
    }


    @Override
    public List<Station> getStations() {
        return Repositories.getH2Repo().getStations();
    }

    @Override
    public List<Track> getTracks() {
        return Repositories.getH2Repo().getTracks();
    }

    @Override
    public List<Reservation> getReservations() {
        return Repositories.getH2Repo().getReservations();
    }
}