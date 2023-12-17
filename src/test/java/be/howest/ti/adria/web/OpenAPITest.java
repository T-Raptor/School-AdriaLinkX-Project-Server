package be.howest.ti.adria.web;

import be.howest.ti.adria.logic.controller.MockController;
import be.howest.ti.adria.logic.data.Repositories;
import be.howest.ti.adria.web.bridge.OpenApiBridge;
import be.howest.ti.adria.web.bridge.RtcBridge;
import io.netty.util.internal.StringUtil;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;

import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
@SuppressWarnings({"PMD.JUnitTestsShouldIncludeAssert","PMD.AvoidDuplicateLiterals"})
/*
 * PMD.JUnitTestsShouldIncludeAssert: VertxExtension style asserts are marked as false positives.
 * PMD.AvoidDuplicateLiterals: Should all be part of the spec (e.g., urls and names of req/res body properties, ...)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OpenAPITest {

    private static final int PORT = 8080;
    private static final String HOST = "localhost";
    public static final String MSG_200_EXPECTED = "If all goes right, we expect a 200 status";
    public static final String MSG_201_EXPECTED = "If a resource is successfully created.";
    public static final String MSG_204_EXPECTED = "If a resource is successfully deleted";
    private Vertx vertx;
    private WebClient webClient;

    @BeforeAll
    void deploy(final VertxTestContext testContext) {
        Repositories.shutdown();
        vertx = Vertx.vertx();

        WebServer webServer = new WebServer(new OpenApiBridge(new MockController()), new RtcBridge());
        vertx.deployVerticle(
                webServer,
                testContext.succeedingThenComplete()
        );
        webClient = WebClient.create(vertx);
    }

    @AfterAll
    void close(final VertxTestContext testContext) {
        vertx.close(testContext.succeedingThenComplete());
        webClient.close();
        Repositories.shutdown();
    }

    @Test
    void getQuote(final VertxTestContext testContext) {
        webClient.get(PORT, HOST, "/api/quotes/1").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    assertTrue(
                            StringUtils.isNotBlank(response.bodyAsJsonObject().getString("value")),
                            "Empty quotes are not allowed"
                    );
                    testContext.completeNow();
                }));
    }

    

    @Test
    void getQuoteWithInvalidID(final VertxTestContext testContext) {
        webClient.get(PORT, HOST, "/api/quotes/333").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    assertTrue(
                            StringUtils.isNotBlank(response.bodyAsJsonObject().getString("value")),
                            "Empty quotes are not allowed"
                    );
                    testContext.completeNow();
                }));
    }

    @Test
    void createQuote(final VertxTestContext testContext) {
        String testQuote = "some value";
        webClient.post(PORT, HOST, "/api/quotes").sendJsonObject(createQuote(testQuote))
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(201, response.statusCode(), MSG_201_EXPECTED);
                    assertEquals(
                            testQuote,
                            response.bodyAsJsonObject().getString("value"),
                            "Quote does not match " + testQuote);
                    testContext.completeNow();
                }));
    }

    @Test
    void updateQuote(final VertxTestContext testContext) {
        String testQuote = "some value";
        webClient.put(PORT, HOST, "/api/quotes/0").sendJsonObject(createQuote(testQuote))
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    assertEquals(
                            testQuote,
                            response.bodyAsJsonObject().getString("value"),
                            "Quote does not match " + testQuote);
                    testContext.completeNow();
                }));
    }

    @Test
    void deleteQuote(final VertxTestContext testContext) {
        webClient.delete(PORT, HOST, "/api/quotes/1").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(204, response.statusCode(), MSG_204_EXPECTED);
                    testContext.completeNow();
                }));
    }

    private JsonObject createQuote(String quote) {
        return new JsonObject().put("quote", quote);
    }


    @Test
    void getStations(final VertxTestContext testContext) {
        webClient.get(PORT, HOST, "/api/stations").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    JsonArray array = response.bodyAsJsonArray();
                    assertFalse(array.isEmpty());
                    for (int i = 0; i < array.size(); i++) {
                        JsonObject body = array.getJsonObject(i);
                        assertFalse(StringUtil.isNullOrEmpty(body.getString("name")));
                        assertNotNull(body.getDouble("latitude"));
                        assertNotNull(body.getDouble("longitude"));
                    }
                    testContext.completeNow();
                }));
    }

    @Test
    void getTracks(final VertxTestContext testContext) {
        webClient.get(PORT, HOST, "/api/tracks").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    JsonArray array = response.bodyAsJsonArray();
                    assertFalse(array.isEmpty());
                    for (int i = 0; i < array.size(); i++) {
                        JsonObject body = array.getJsonObject(i);
                        assertNotNull(body.getJsonObject("station1"));
                        assertNotNull(body.getJsonObject("station2"));
                    }
                    testContext.completeNow();
                }));
    }

    @Test
    void getReservations(final VertxTestContext testContext) {
        webClient.get(PORT, HOST, "/api/reservations").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    JsonArray array = response.bodyAsJsonArray();
                    assertFalse(array.isEmpty());
                    for (int i = 0; i < array.size(); i++) {
                        LOGGER.log(Level.INFO, array.getJsonObject(i).toString());
                        JsonObject body = array.getJsonObject(i);
                        assertNotNull(body.getString("periodStart"));
                        assertNotNull(body.getString("periodStop"));
                        assertNotNull(body.getString("company"));
                        assertNotNull(body.getJsonArray("route"));
                    }
                    testContext.completeNow();
                }));
    }

    @Test
    void searchEventsNoFilter(final VertxTestContext testContext) {
        webClient.get(PORT, HOST, "/api/events").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    JsonArray array = response.bodyAsJsonArray();
                    assertFalse(array.isEmpty());
                    for (int i = 0; i < array.size(); i++) {
                        LOGGER.log(Level.INFO, array.getJsonObject(i).toString());
                        JsonObject body = array.getJsonObject(i);
                        assertNotNull(body.getString("moment"));
                        assertNotNull(body.getString("target"));
                        assertNotNull(body.getString("subject"));
                    }
                    testContext.completeNow();
                }));
    }

    @Test
    void searchEventsFilterSubject(final VertxTestContext testContext) {
        webClient.get(PORT, HOST, "/api/events?subject=MOVE").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    JsonArray array = response.bodyAsJsonArray();
                    assertFalse(array.isEmpty());
                    for (int i = 0; i < array.size(); i++) {
                        LOGGER.log(Level.INFO, array.getJsonObject(i).toString());
                        JsonObject body = array.getJsonObject(i);
                        assertNotNull(body.getString("moment"));
                        assertNotNull(body.getString("target"));
                        assertNotNull(body.getString("subject"));
                        assertEquals("MOVE", body.getString("subject"));
                    }
                    testContext.completeNow();
                }));
    }

    @Test
    void searchEventsFilterEarliest(final VertxTestContext testContext) {
        Timestamp earliest = new Timestamp(1500);
        webClient.get(PORT, HOST, "/api/events?earliest=1500").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    JsonArray array = response.bodyAsJsonArray();
                    assertFalse(array.isEmpty());
                    for (int i = 0; i < array.size(); i++) {
                        LOGGER.log(Level.INFO, array.getJsonObject(i).toString());
                        JsonObject body = array.getJsonObject(i);
                        assertNotNull(body.getString("target"));
                        assertNotNull(body.getString("subject"));
                        Timestamp moment = new Timestamp(body.getLong("moment"));
                        assertTrue(earliest.before(moment) || earliest.equals(moment));
                    }
                    testContext.completeNow();
                }));
    }

    @Test
    void searchEventsFilterLatest(final VertxTestContext testContext) {
        Timestamp latest = new Timestamp(1500);
        webClient.get(PORT, HOST, "/api/events?latest=1500").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    JsonArray array = response.bodyAsJsonArray();
                    assertFalse(array.isEmpty());
                    for (int i = 0; i < array.size(); i++) {
                        LOGGER.log(Level.INFO, array.getJsonObject(i).toString());
                        JsonObject body = array.getJsonObject(i);
                        assertNotNull(body.getString("moment"));
                        assertNotNull(body.getString("target"));
                        assertNotNull(body.getString("subject"));
                        Timestamp moment = new Timestamp(body.getLong("moment"));
                        assertTrue(latest.after(moment) || latest.equals(moment));
                    }
                    testContext.completeNow();
                }));
    }

    @Test
    void searchEventsFilterTarget(final VertxTestContext testContext) {
        int target = 2;
        webClient.get(PORT, HOST, "/api/events?target=2").send()
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    JsonArray array = response.bodyAsJsonArray();
                    assertFalse(array.isEmpty());
                    for (int i = 0; i < array.size(); i++) {
                        LOGGER.log(Level.INFO, array.getJsonObject(i).toString());
                        JsonObject body = array.getJsonObject(i);
                        assertNotNull(body.getString("moment"));
                        assertNotNull(body.getString("target"));
                        assertNotNull(body.getString("subject"));
                        assertEquals(target, body.getJsonObject("target").getInteger("id"));
                    }
                    testContext.completeNow();
                }));
    }

    private JsonObject createLocalEventProposal(int target, long moment, String subject, double latitude, double longitude) {
        return new JsonObject()
                .put("target", target)
                .put("moment", moment)
                .put("subject", subject)
                .put("latitude", latitude)
                .put("longitude", longitude);
    }

    private JsonObject createBasicEventProposal(int target, long moment, String subject) {
        return new JsonObject()
                .put("target", target)
                .put("moment", moment)
                .put("subject", subject);
    }

    @Test
    void pushLocalEvent(final VertxTestContext testContext) {
        int target = 9;
        long moment = Timestamp.valueOf("2022-05-13 09:40:09").getTime();
        String subject = "MOVE";
        double latitude = 47;
        double longitude = -25;
        webClient.post(PORT, HOST, "/api/events").sendJsonObject(createLocalEventProposal(target, moment, subject, latitude, longitude))
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    JsonObject event = response.bodyAsJsonObject();
                    assertEquals(target, event.getJsonObject("target").getInteger("id"));
                    assertEquals(moment, event.getLong("moment"));
                    assertEquals(subject, event.getString("subject"));
                    assertEquals(latitude, event.getDouble("latitude"));
                    assertEquals(longitude, event.getDouble("longitude"));
                    testContext.completeNow();
                }));
    }

    @Test
    void pushBasicEvent(final VertxTestContext testContext) {
        int target = 3;
        long moment = Timestamp.valueOf("2022-05-13 09:40:09").getTime();
        String subject = "BREAK";
        webClient.post(PORT, HOST, "/api/events").sendJsonObject(createBasicEventProposal(target, moment, subject))
                .onFailure(testContext::failNow)
                .onSuccess(response -> testContext.verify(() -> {
                    assertEquals(200, response.statusCode(), MSG_200_EXPECTED);
                    JsonObject event = response.bodyAsJsonObject();
                    assertEquals(target, event.getJsonObject("target").getInteger("id"));
                    assertEquals(moment, event.getLong("moment"));
                    assertEquals(subject, event.getString("subject"));
                    testContext.completeNow();
                }));
    }

    private static final Logger LOGGER = Logger.getLogger(OpenAPITest.class.getName());
}