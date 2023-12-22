package be.howest.ti.adria.web.bridge;

import be.howest.ti.adria.logic.controller.DefaultController;
import be.howest.ti.adria.logic.controller.Controller;
import be.howest.ti.adria.logic.domain.EventFilter;
import be.howest.ti.adria.logic.domain.*;
import be.howest.ti.adria.logic.domain.observables.Reservation;
import be.howest.ti.adria.logic.domain.observables.Shuttle;
import be.howest.ti.adria.logic.domain.observables.Station;
import be.howest.ti.adria.logic.domain.observables.Track;
import be.howest.ti.adria.logic.domain.proposals.EventProposal;
import be.howest.ti.adria.logic.domain.proposals.ReservationProposal;
import be.howest.ti.adria.logic.domain.proposals.ShuttleProposal;
import be.howest.ti.adria.web.exceptions.MalformedRequestException;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.openapi.RouterBuilder;

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

        LOGGER.log(Level.INFO, "Installing handler for: getStations");
        routerBuilder.operation("getStations").handler(this::getStations);

        LOGGER.log(Level.INFO, "Installing handler for: getTracks");
        routerBuilder.operation("getTracks").handler(this::getTracks);

        LOGGER.log(Level.INFO, "Installing handler for: getReservations");
        routerBuilder.operation("getReservations").handler(this::getReservations);

        LOGGER.log(Level.INFO, "Installing handler for: searchEvents");
        routerBuilder.operation("searchEvents").handler(this::searchEvents);

        LOGGER.log(Level.INFO, "Installing handler for: pushEvent");
        routerBuilder.operation("pushEvent").handler(this::pushEvent);

        LOGGER.log(Level.INFO, "Installing handler for: placeReservation");
        routerBuilder.operation("createReservation").handler(this::placeReservation);

        LOGGER.log(Level.INFO, "Installing handler for: popUnreadNotifications");
        routerBuilder.operation("popUnreadNotifications").handler(this::popUnreadNotifications);

        LOGGER.log(Level.INFO, "Installing handler for: getShuttles");
        routerBuilder.operation("getShuttles").handler(this::getShuttles);

        LOGGER.log(Level.INFO, "Installing handler for: registerShuttle");
        routerBuilder.operation("registerShuttle").handler(this::registerShuttle);

        LOGGER.log(Level.INFO, "All handlers are installed, creating router.");
        return routerBuilder.createRouter();
    }

    public OpenApiBridge() {
        this.controller = new DefaultController();
    }

    public OpenApiBridge(Controller controller) {
        this.controller = controller;
    }

    public void getStations(RoutingContext ctx) {
        List<Station> stations = controller.getStations();
        Response.sendStations(ctx, stations);
    }

    public void getTracks(RoutingContext ctx) {
        List<Track> tracks = controller.getTracks();
        Response.sendTracks(ctx, tracks);
    }

    public void getShuttles(RoutingContext ctx) {
        List<Shuttle> shuttles = controller.getShuttles();
        Response.sendShuttles(ctx, shuttles);
    }

    public void registerShuttle(RoutingContext ctx) {
        ShuttleProposal proposal = Request.from(ctx).getShuttleProposal();
        Shuttle shuttle = controller.registerShuttle(proposal);
        Response.sendShuttle(ctx, shuttle);
    }

    public void getReservations(RoutingContext ctx) {
        List<Reservation> reservations = controller.getReservations();
        Response.sendReservations(ctx, reservations);
    }

    public void searchEvents(RoutingContext ctx) {
        EventFilter filter = Request.from(ctx).getEventFilter();
        List<Event> events = controller.searchEvents(filter);
        Response.sendEvents(ctx, events);
    }

    public void pushEvent(RoutingContext ctx) {
        EventProposal proposal = Request.from(ctx).getEventProposal();
        Event event = controller.pushEvent(proposal);
        Response.sendEvent(ctx, event);
    }

    public void placeReservation(RoutingContext ctx) {
        ReservationProposal proposal = Request.from(ctx).getReservationProposal();
        Reservation reservation = controller.placeReservation(proposal);
        Response.sendReservationCreated(ctx, reservation);
    }

    public void popUnreadNotifications(RoutingContext ctx) {
        String company = Request.from(ctx).getCompany();
        List<Notification> notifications = controller.popUnreadNotifications(company);
        Response.sendNotifications(ctx, notifications);
    }


    private void onFailedRequest(RoutingContext ctx) {
        Throwable cause = ctx.failure();
        int code = ctx.statusCode();
        String message = Objects.isNull(cause) ? "" + code : cause.getMessage();

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

        Response.sendFailure(ctx, code, message);
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
