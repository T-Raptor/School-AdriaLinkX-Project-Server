package be.howest.ti.adria.web.bridge;

import be.howest.ti.adria.logic.domain.Quote;
import be.howest.ti.adria.logic.domain.Station;
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

    public static void sendQuote(RoutingContext ctx, Quote quote) {
        sendOkJsonResponse(ctx, JsonObject.mapFrom(quote));
    }

    public static void sendQuoteCreated(RoutingContext ctx, Quote quote) {
        sendJsonResponse(ctx, 201, JsonObject.mapFrom(quote));
    }

    public static void sendQuoteDeleted(RoutingContext ctx) {
        sendEmptyResponse(ctx, 204);
    }

    public static void sendQuoteUpdated(RoutingContext ctx, Quote quote) {
        sendOkJsonResponse(ctx, JsonObject.mapFrom(quote));
    }

    private static void sendOkJsonResponse(RoutingContext ctx, JsonObject response) {
        sendJsonResponse(ctx, 200, response);
    }

    private static void sendOkJsonResponse(RoutingContext ctx, String response) {
        sendJsonResponse(ctx, 200, response);
    }

    private static void sendEmptyResponse(RoutingContext ctx, int statusCode) {
        ctx.response()
                .setStatusCode(statusCode)
                .end();
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

    public static void sendFailure(RoutingContext ctx, int code, String quote) {
        sendJsonResponse(ctx, code, new JsonObject()
                .put("failure", code)
                .put("cause", quote));
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
}
