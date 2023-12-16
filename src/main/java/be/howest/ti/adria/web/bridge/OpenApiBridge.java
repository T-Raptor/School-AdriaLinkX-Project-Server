package be.howest.ti.adria.web.bridge;

import be.howest.ti.adria.logic.controller.DefaultController;
import be.howest.ti.adria.logic.controller.Controller;
import be.howest.ti.adria.logic.domain.Quote;
import be.howest.ti.adria.logic.domain.Reservation;
import be.howest.ti.adria.logic.domain.Station;
import be.howest.ti.adria.logic.domain.Track;
import be.howest.ti.adria.web.exceptions.MalformedRequestException;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.openapi.RouterBuilder;

import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * In the AdriaOpenApiBridge class you will create one handler-method per API operation.
 * The job of the "bridge" is to bridge between JSON (request and response) and Java (the controller).
 * <p>
 * For each API operation you should get the required data from the `Request` class.
 * The Request class will turn the HTTP request data into the desired Java types (int, String, Custom class,...)
 * This desired type is then passed to the controller.
 * The return value of the controller is turned to Json or another Web data type in the `Response` class.
 */
public class OpenApiBridge {
    private static final Logger LOGGER = Logger.getLogger(OpenApiBridge.class.getName());
    private final Controller controller;

    public Router buildRouter(RouterBuilder routerBuilder) {
        LOGGER.log(Level.INFO, "Installing cors handlers");
        routerBuilder.rootHandler(createCorsHandler());

        LOGGER.log(Level.INFO, "Installing failure handlers for all operations");
        routerBuilder.operations().forEach(op -> op.failureHandler(this::onFailedRequest));

        LOGGER.log(Level.INFO, "Installing handler for: getQuote");
        routerBuilder.operation("getQuote").handler(this::getQuote);

        LOGGER.log(Level.INFO, "Installing handler for: createQuote");
        routerBuilder.operation("createQuote").handler(this::createQuote);

        LOGGER.log(Level.INFO, "Installing handler for: updateQuote");
        routerBuilder.operation("updateQuote").handler(this::updateQuote);

        LOGGER.log(Level.INFO, "Installing handler for: deleteQuote");
        routerBuilder.operation("deleteQuote").handler(this::deleteQuote);

        LOGGER.log(Level.INFO, "Installing handler for: getStations");
        routerBuilder.operation("getStations").handler(this::getStations);

        LOGGER.log(Level.INFO, "Installing handler for: getTracks");
        routerBuilder.operation("getTracks").handler(this::getTracks);

        LOGGER.log(Level.INFO, "All handlers are installed, creating router.");
        return routerBuilder.createRouter();


    }

    public OpenApiBridge() {
        this.controller = new DefaultController();
    }

    public OpenApiBridge(Controller controller) {
        this.controller = controller;
    }

    public void getQuote(RoutingContext ctx) {
        Quote quote = controller.getQuote(Request.from(ctx).getQuoteId());

        Response.sendQuote(ctx, quote);
    }

    public void createQuote(RoutingContext ctx) {
        String quote = Request.from(ctx).getQuote();

        Response.sendQuoteCreated(ctx, controller.createQuote(quote));
    }

    public void updateQuote(RoutingContext ctx) {
        Request request = Request.from(ctx);
        String quoteValue = request.getQuote();
        int quoteId = request.getQuoteId();

        Response.sendQuoteUpdated(ctx, controller.updateQuote(quoteId, quoteValue));
    }

    public void deleteQuote(RoutingContext ctx) {
        int quoteId = Request.from(ctx).getQuoteId();

        controller.deleteQuote(quoteId);

        Response.sendQuoteDeleted(ctx);
    }


    public void getStations(RoutingContext ctx) {
        List<Station> stations = controller.getStations();
        Response.sendStations(ctx, stations);
    }

    public void getTracks(RoutingContext ctx) {
        List<Track> tracks = controller.getTracks();
        Response.sendTracks(ctx, tracks);
    }

    public void insertReservation(RoutingContext ctx) {
        try {
            // Extract data from the request
            Request request = Request.from(ctx);
            if (!request.bodyIsEmpty()) {
                Timestamp periodStart = request.getPeriodStart();
                Timestamp periodStop = request.getPeriodStop();
                String company = request.getCompany();
                List<Track> route = request.getRoute();

                // Create the reservation
                Reservation reservation = controller.insertReservation(periodStart, periodStop, company, route);

                // Send the reservation back to the client
                Response.sendReservationCreated(ctx, reservation);
            }
                throw new MalformedRequestException("Request body is not empty");
        } catch (Exception e) {
            Response.sendFailure(ctx, 400, e.getMessage());
        }
    }


    private void onFailedRequest(RoutingContext ctx) {
        Throwable cause = ctx.failure();
        int code = ctx.statusCode();
        String quote = Objects.isNull(cause) ? "" + code : cause.getMessage();

        // Map custom runtime exceptions to a HTTP status code.
        LOGGER.log(Level.INFO, "Failed request", cause);
        if (cause instanceof IllegalArgumentException) {
            code = 400;
        } else if (cause instanceof MalformedRequestException) {
            code = 400;
        } else if (cause instanceof NoSuchElementException) {
            code = 404;
        } else {
            LOGGER.log(Level.WARNING, "Failed request", cause);
        }

        Response.sendFailure(ctx, code, quote);
    }

    private CorsHandler createCorsHandler() {
        return CorsHandler.create(".*.")
                .allowedHeader("x-requested-with")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Credentials")
                .allowCredentials(true)
                .allowedHeader("origin")
                .allowedHeader("Content-Type")
                .allowedHeader("Authorization")
                .allowedHeader("accept")
                .allowedMethod(HttpMethod.HEAD)
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedMethod(HttpMethod.PATCH)
                .allowedMethod(HttpMethod.DELETE)
                .allowedMethod(HttpMethod.PUT);
    }
}
