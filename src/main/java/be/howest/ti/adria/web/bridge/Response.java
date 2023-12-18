package be.howest.ti.adria.web.bridge;

import be.howest.ti.adria.logic.domain.*;
import be.howest.ti.adria.web.exceptions.BridgeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Response class is responsible for translating the result of the controller into
 * JSON responses with an appropriate HTTP code.
 */
public class Response {
    private static final Logger LOGGER = Logger.getLogger(Response.class.getName());

    private Response() { }

    private static void sendOkJsonResponse(RoutingContext ctx, JsonObject response) {
        sendJsonResponse(ctx, 200, response);
    }

    private static void sendOkJsonResponse(RoutingContext ctx, String response) {
        sendJsonResponse(ctx, 200, response);
    }

    private static void sendJsonResponse(RoutingContext ctx, int statusCode, Object response) {
        ctx.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setStatusCode(statusCode)
                .end(Json.encodePrettily(response));
    }

    private static void sendJsonResponse(RoutingContext ctx, int statusCode, String response) {
        ctx.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setStatusCode(statusCode)
                .end(response);
    }

    public static void sendFailure(RoutingContext ctx, int code, String message) {
        sendJsonResponse(ctx, code, new JsonObject()
                .put("failure", code)
                .put("cause", message));
    }

    public static void sendStations(RoutingContext ctx, List<Station> stations) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonArray = objectMapper.writeValueAsString(stations);
            sendOkJsonResponse(ctx, jsonArray);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Error occurred while attempting to send stations", e);
            throw new BridgeException("Error occurred while attempting to send stations");
        }
    }

    public static void sendTracks(RoutingContext ctx, List<Track> tracks) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonArray = objectMapper.writeValueAsString(tracks);
            sendOkJsonResponse(ctx, jsonArray);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Error occurred while attempting to send tracks", e);
            throw new BridgeException("Error occurred while attempting to send tracks");
        }
    }

    public static void sendReservations(RoutingContext ctx, List<Reservation> reservations) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonArray = objectMapper.writeValueAsString(reservations);
            sendOkJsonResponse(ctx, jsonArray);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Error occurred while attempting to send reservations", e);
            throw new BridgeException("Error occurred while attempting to send reservations");
        }
    }

    public static void sendEvents(RoutingContext ctx, List<Event> events) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonArray = objectMapper.writeValueAsString(events);
            sendOkJsonResponse(ctx, jsonArray);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Error occurred while attempting to send events", e);
            throw new BridgeException("Error occurred while attempting to send events");
        }
    }

    public static void sendEvent(RoutingContext ctx, Event event) {
        sendOkJsonResponse(ctx, JsonObject.mapFrom(event));
    }

    public static void sendReservationCreated(RoutingContext ctx, Reservation reservation) {
        sendJsonResponse(ctx, 201, JsonObject.mapFrom(reservation));

    }

    public static void sendNotifications(RoutingContext ctx, List<Notification> notifications) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonArray = objectMapper.writeValueAsString(notifications);
            sendOkJsonResponse(ctx, jsonArray);
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Error occurred while attempting to send notifications", e);
            throw new BridgeException("Error occurred while attempting to send notifications");
        }
    }
}
