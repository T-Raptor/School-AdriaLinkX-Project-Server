package be.howest.ti.adria.logic.controller;

import be.howest.ti.adria.logic.domain.Quote;

public interface Controller {
    Quote getQuote(int quoteId);

    Quote createQuote(String quote);

    Quote updateQuote(int quoteId, String quote);

    void deleteQuote(int quoteId);
}
