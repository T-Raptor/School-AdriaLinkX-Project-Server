package be.howest.ti.adria.web.bridge;

import be.howest.ti.adria.logic.controller.EventFilter;
import be.howest.ti.adria.logic.domain.*;
import be.howest.ti.adria.web.exceptions.MalformedRequestException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.validation.RequestParameters;
import io.vertx.ext.web.validation.ValidationHandler;

import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Request class is responsible for translating information that is part of the
 * request into Java.
 *
 * For every piece of information that you need from the request, you should provide a method here.
 * You can find information in:
 * - the request path: params.pathParameter("some-param-name")
 * - the query-string: params.queryParameter("some-param-name")
 * Both return a `RequestParameter`, which can contain a string or an integer in our case.
 * The actual data can be retrieved using `getInteger()` or `getString()`, respectively.
 * You can check if it is an integer (or not) using `isNumber()`.
 *
 * Finally, some requests have a body. If present, the body will always be in the json format.
 * You can acces this body using: `params.body().getJsonObject()`.
 *
 * **TIP:** Make sure that al your methods have a unique name. For instance, there is a request
 * that consists of more than one "player name". You cannot use the method `getPlayerName()` for both,
 * you will need a second one with a different name.
 */
public class Request {
    private static final Logger LOGGER = Logger.getLogger(Request.class.getName());
    public static final String SPEC_QUOTE_ID = "quoteId";
    public static final String SPEC_QUOTE = "quote";

    private static final String MSG_BODY_PARSING_FAILED = "Unable to decipher data in the body";
    private static final String MSG_BODY_PARSING_FAILED_EXT = "Unable to decipher data in the body. See logs for details.";
    private final RequestParameters params;

    public static Request from(RoutingContext ctx) {
        return new Request(ctx);
    }

    private Request(RoutingContext ctx) {
        this.params = ctx.get(ValidationHandler.REQUEST_CONTEXT_KEY);
    }

    public int getQuoteId() {
        return params.pathParameter(SPEC_QUOTE_ID).getInteger();
    }

    public String getQuote() {
        try {
            if (params.body().isJsonObject())
                return params.body().getJsonObject().getString(SPEC_QUOTE);
            return params.body().get().toString();
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.INFO, MSG_BODY_PARSING_FAILED, ex);
            throw new MalformedRequestException(MSG_BODY_PARSING_FAILED_EXT);
        }
    }

    public EventFilter getEventFilter() {
        try {
            EventFilter filter = new EventFilter();

            if (params.queryParameter("target") != null) {
                filter.setTarget(new UnknownObservable(params.queryParameter("target").getInteger()));
            }
            if (params.queryParameter("earliest") != null) {
                filter.setEarliest(new Timestamp(params.queryParameter("earliest").getLong()));
            }
            if (params.queryParameter("latest") != null) {
                filter.setLatest(new Timestamp(params.queryParameter("latest").getLong()));
            }
            if (params.queryParameter("subject") != null) {
                filter.setSubject(params.queryParameter("subject").getString());
            }

            return filter;
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.INFO, MSG_BODY_PARSING_FAILED, ex);
            throw new MalformedRequestException(MSG_BODY_PARSING_FAILED_EXT);
        }
    }

    public EventProposal getEventProposal() {
        try {
            if (!params.body().isJsonObject()) {
                throw new MalformedRequestException("Body is not a json object");
            }
            JsonObject jsonProposal = params.body().getJsonObject();
            EventProposal proposal;
            if (jsonProposal.containsKey("latitude") || jsonProposal.containsKey("longitude")) {
                proposal = jsonProposal.mapTo(LocalEventProposal.class);
            } else {
                proposal = jsonProposal.mapTo(EventProposal.class);
            }
            return proposal;
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.INFO, MSG_BODY_PARSING_FAILED, ex);
            throw new MalformedRequestException(MSG_BODY_PARSING_FAILED_EXT);
        }
    }

    public ReservationProposal getReservationProposal() {
        try {
            if (!params.body().isJsonObject()) {
                throw new MalformedRequestException("Body is not a json object");
            }
            JsonObject jsonProposal = params.body().getJsonObject();
            return jsonProposal.mapTo(ReservationProposal.class);
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.INFO, MSG_BODY_PARSING_FAILED, ex);
            throw new MalformedRequestException(MSG_BODY_PARSING_FAILED_EXT);
        }
    }
}
