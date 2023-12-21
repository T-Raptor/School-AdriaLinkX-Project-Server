package be.howest.ti.adria.logic.controller;

import be.howest.ti.adria.logic.data.Repositories;
import be.howest.ti.adria.logic.domain.*;
import be.howest.ti.adria.logic.domain.observables.*;
import be.howest.ti.adria.logic.domain.proposals.*;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultControllerTest {

    private static final String URL = "jdbc:h2:./db-12";

    @BeforeAll
    void setupTestSuite() {
        Repositories.shutdown();
        JsonObject dbProperties = new JsonObject(Map.of("url", "jdbc:h2:./db-12",
                "username", "",
                "password", "",
                "webconsole.port", 9000));
        Repositories.configure(dbProperties);
    }

    @BeforeEach
    void setupTest() {
        Repositories.getH2Repo().generateData();
    }

    @Test
    void getStations() {
        // Arrange
        Controller sut = new DefaultController();

        // Act
        List<Station> stations = sut.getStations();

        //Assert
        assertNotNull(stations);
        assertFalse(stations.isEmpty());
    }

    @Test
    void getTracks() {
        // Arrange
        Controller sut = new DefaultController();

        // Act
        List<Track> tracks = sut.getTracks();

        //Assert
        assertNotNull(tracks);
        assertFalse(tracks.isEmpty());
    }

    @Test
    void getShuttles() {
        // Arrange
        Controller sut = new DefaultController();

        // Act
        List<Shuttle> shuttles = sut.getShuttles();

        //Assert
        assertNotNull(shuttles);
        assertFalse(shuttles.isEmpty());
    }

    @Test
    void registerShuttle() {
        // Arrange
        String serial = "AAAA-BBBB-CCCC";
        Controller sut = new DefaultController();
        ShuttleProposal proposal = new ShuttleProposal(serial);

        // Act
        Shuttle shuttle = sut.registerShuttle(proposal);

        //Assert
        assertNotNull(shuttle);
        assertEquals(serial, shuttle.getSerial());
    }

    @Test
    void getReservations() {
        // Arrange
        Controller sut = new DefaultController();

        // Act
        List<Reservation> reservations = sut.getReservations();

        //Assert
        assertNotNull(reservations);
        assertFalse(reservations.isEmpty());
    }

    @Test
    void searchEventsNoFilter() {
        // Arrange
        Controller sut = new DefaultController();
        EventFilter filter = new EventFilter();

        // Act
        List<Event> events = sut.searchEvents(filter);

        //Assert
        assertNotNull(events);
        assertFalse(events.isEmpty());
    }

    @Test
    void searchEventsFilterEarliest() {
        // Arrange
        Timestamp earliest = Timestamp.valueOf("2022-5-13 09:40:09");
        Controller sut = new DefaultController();
        EventFilter filter = new EventFilter();
        filter.setEarliest(earliest);

        // Act
        List<Event> events = sut.searchEvents(filter);

        //Assert
        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertTrue(events.stream().allMatch(x -> earliest.before(x.getMoment()) || earliest.equals(x.getMoment()) ));
    }

    @Test
    void searchEventsFilterLatest() {
        // Arrange
        Timestamp latest = Timestamp.valueOf("2022-5-13 09:40:09");
        Controller sut = new DefaultController();
        EventFilter filter = new EventFilter();
        filter.setLatest(latest);

        // Act
        List<Event> events = sut.searchEvents(filter);

        //Assert
        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertTrue(events.stream().allMatch(x -> latest.after(x.getMoment()) || latest.equals(x.getMoment()) ));
    }

    @Test
    void searchEventsFilterTarget() {
        // Arrange
        Observable target = new UnknownObservable(4);
        Controller sut = new DefaultController();
        EventFilter filter = new EventFilter();
        filter.setTarget(target);

        // Act
        List<Event> events = sut.searchEvents(filter);

        //Assert
        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertTrue(events.stream().allMatch(x -> target.equals(x.getTarget())));
    }

    @Test
    void searchEventsFilterSubject() {
        // Arrange
        String subject = "MOVE";
        Controller sut = new DefaultController();
        EventFilter filter = new EventFilter();
        filter.setSubject(subject);

        // Act
        List<Event> events = sut.searchEvents(filter);

        //Assert
        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertTrue(events.stream().allMatch(x -> subject.equals(x.getSubject())));
    }

    @Test
    void pushBasicEvent() {
        // Arrange
        int target = 9;
        Timestamp moment = Timestamp.valueOf("2022-5-13 09:40:09");
        String subject = "MOVE";
        Controller sut = new DefaultController();
        EventProposal proposal = new EventProposal(target, moment, subject);

        // Act
        Event event = sut.pushEvent(proposal);

        //Assert
        assertNotNull(event);
        assertNotNull(event.getTarget());
        assertEquals(target, event.getTarget().getId());
        assertEquals(moment, event.getMoment());
        assertEquals(subject, event.getSubject());
    }

    @Test
    void pushLocalEvent() {
        // Arrange
        int target = 9;
        Timestamp moment = Timestamp.valueOf("2022-5-13 09:40:09");
        String subject = "MOVE";
        double latitude = 5;
        double longitude = -6;
        Controller sut = new DefaultController();
        LocalEventProposal proposal = new LocalEventProposal(target, moment, subject, latitude, longitude);

        // Act
        Event event = sut.pushEvent(proposal);

        //Assert
        assertNotNull(event);
        assertNotNull(event.getTarget());
        assertEquals(target, event.getTarget().getId());
        assertEquals(moment, event.getMoment());
        assertEquals(subject, event.getSubject());
        assertInstanceOf(LocalEvent.class, event);
        assertEquals(latitude, ((LocalEvent)event).getLatitude());
        assertEquals(longitude, ((LocalEvent)event).getLongitude());
    }

    @Test
    void placeReservation() {
        // Arrange
        Timestamp periodStart = Timestamp.valueOf("2022-5-13 09:40:09");
        Timestamp periodStop = Timestamp.valueOf("2022-5-13 10:40:09");
        String company = "Macrosoft";
        List<Integer> route = List.of(4, 5);
        Controller sut = new DefaultController();
        ReservationProposal proposal = new ReservationProposal(periodStart, periodStop, company, route);

        // Act
        Reservation reservation = sut.placeReservation(proposal);

        //Assert
        assertNotNull(reservation);
        assertEquals(periodStart, reservation.getPeriodStart());
        assertEquals(periodStop, reservation.getPeriodStop());
        assertEquals(company, reservation.getCompany());

        assertEquals(route.size(), reservation.getRoute().size());
        for (int i = 0; i < reservation.getRoute().size(); i++) {
            int testTrackId = route.get(i);
            int realTrackId = reservation.getRoute().get(i).getId();
            assertEquals(testTrackId, realTrackId);
        }
    }

    @Test
    void popUnreadNotifications() {
        // Arrange
        String company = "Macrosoft";
        Controller sut = new DefaultController();

        // Act
        List<Notification> notifications = sut.popUnreadNotifications(company);

        //Assert
        assertNotNull(notifications);
        assertFalse(notifications.isEmpty());
        for (Notification notification : notifications) {
            assertEquals(company, notification.getCompany());
            assertFalse(notification.isRead());
        }
    }

    @Test
    void popUnreadNotificationsFlushes() {
        // Arrange
        String company = "Macrosoft";
        Controller sut = new DefaultController();
        sut.popUnreadNotifications(company);

        // Act
        List<Notification> notifications = sut.popUnreadNotifications(company);

        //Assert
        assertNotNull(notifications);
        assertTrue(notifications.isEmpty());
    }

    @Test
    void pushEventNotificationWarn() {
        // Arrange
        String company = "Xyzerty";
        Controller sut = new DefaultController();
        Reservation reservation = sut.placeReservation(new ReservationProposal(
                new Timestamp(1000),
                new Timestamp(2000),
                "Xyzerty",
                List.of(4, 5)
        ));
        sut.popUnreadNotifications(company);

        // Act
        sut.pushEvent(new EventProposal(4, new Timestamp(1500), "WARN"));

        // Assert
        List<Notification> notifications = sut.popUnreadNotifications(company);
        assertFalse(notifications.isEmpty());
    }

    @Test
    void pushEventNotificationBreak() {
        // Arrange
        String company = "Xyzerty";
        Controller sut = new DefaultController();
        Reservation reservation = sut.placeReservation(new ReservationProposal(
                new Timestamp(1000),
                new Timestamp(2000),
                "Xyzerty",
                List.of(4, 5)
        ));
        sut.popUnreadNotifications(company);

        // Act
        sut.pushEvent(new EventProposal(4, new Timestamp(1500), "BREAK"));

        // Assert
        List<Notification> notifications = sut.popUnreadNotifications(company);
        assertFalse(notifications.isEmpty());
    }

    @Test
    void pushEventNoNotificationTooEarly() {
        // Arrange
        String company = "Xyzerty";
        Controller sut = new DefaultController();
        Reservation reservation = sut.placeReservation(new ReservationProposal(
                new Timestamp(1000),
                new Timestamp(2000),
                "Xyzerty",
                List.of(4, 5)
        ));
        sut.popUnreadNotifications(company);

        // Act
        sut.pushEvent(new EventProposal(4, new Timestamp(500), "WARN"));

        // Assert
        List<Notification> notifications = sut.popUnreadNotifications(company);
        assertTrue(notifications.isEmpty());
    }

    @Test
    void pushEventNoNotificationTooLate() {
        // Arrange
        String company = "Xyzerty";
        Controller sut = new DefaultController();
        Reservation reservation = sut.placeReservation(new ReservationProposal(
                new Timestamp(1000),
                new Timestamp(2000),
                "Xyzerty",
                List.of(4, 5)
        ));
        sut.popUnreadNotifications(company);

        // Act
        sut.pushEvent(new EventProposal(4, new Timestamp(2500), "WARN"));

        // Assert
        List<Notification> notifications = sut.popUnreadNotifications(company);
        assertTrue(notifications.isEmpty());
    }

    @Test
    void pushEventNoNotificationWrongSubject() {
        // Arrange
        String company = "Xyzerty";
        Controller sut = new DefaultController();
        Reservation reservation = sut.placeReservation(new ReservationProposal(
                new Timestamp(1000),
                new Timestamp(2000),
                "Macroogle",
                List.of(4, 5)
        ));
        sut.popUnreadNotifications(company);

        // Act
        sut.pushEvent(new EventProposal(4, new Timestamp(1500), "WARN"));

        // Assert
        List<Notification> notifications = sut.popUnreadNotifications(company);
        assertTrue(notifications.isEmpty());
    }

    @Test
    void pushEventNoNotificationWrongTrack() {
        // Arrange
        String company = "Xyzerty";
        Controller sut = new DefaultController();
        Reservation reservation = sut.placeReservation(new ReservationProposal(
                new Timestamp(1000),
                new Timestamp(2000),
                "Xyzerty",
                List.of(5)
        ));
        sut.popUnreadNotifications(company);

        // Act
        sut.pushEvent(new EventProposal(4, new Timestamp(1500), "WARN"));

        // Assert
        List<Notification> notifications = sut.popUnreadNotifications(company);
        assertTrue(notifications.isEmpty());
    }
}
