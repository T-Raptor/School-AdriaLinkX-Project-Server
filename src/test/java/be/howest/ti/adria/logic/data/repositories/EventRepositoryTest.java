package be.howest.ti.adria.logic.data.repositories;

import be.howest.ti.adria.logic.data.repositories.EventRepository;
import be.howest.ti.adria.logic.data.repositories.ShuttleRepository;
import be.howest.ti.adria.logic.data.repositories.TrackRepository;
import be.howest.ti.adria.logic.domain.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;

public abstract class EventRepositoryTest {
    protected TrackRepository trackRepository;
    protected ShuttleRepository shuttleRepository;
    protected EventRepository repository;

    @Test
    void getEvents() {
        // Arrange

        // Act
        List<Event> events = repository.getEvents();

        // Assert
        Assertions.assertNotNull(events);
        Assertions.assertFalse(events.isEmpty());
    }

    @Test
    void getLocalEvents() {
        // Arrange

        // Act
        List<LocalEvent> events = repository.getEvents()
                .stream()
                .filter(ev -> ev instanceof LocalEvent)
                .map(ev -> (LocalEvent)ev)
                .toList();

        // Assert
        Assertions.assertNotNull(events);
        Assertions.assertFalse(events.isEmpty());
    }

    @Test
    void getEvent() {
        // Arrange
        int id = 1;

        // Act
        Event event = repository.getEvent(id);

        // Assert
        Assertions.assertNotNull(event);
        Assertions.assertNotNull(event.getTarget());
        Assertions.assertNotNull(event.getMoment());
        Assertions.assertNotNull(event.getSubject());
    }

    @Test
    void getLocalEvent() {
        // Arrange
        int id = 3;

        // Act
        Event event = repository.getEvent(id);

        // Assert
        Assertions.assertNotNull(event);
        Assertions.assertNotNull(event.getTarget());
        Assertions.assertNotNull(event.getMoment());
        Assertions.assertNotNull(event.getSubject());
        Assertions.assertTrue(event instanceof LocalEvent);
    }

    @Test
    void insertEvent() {
        // Arrange
        Observable target = trackRepository.getTracks().get(0);
        Timestamp moment = Timestamp.valueOf("2023-08-19 12:00:05");
        String subject = "break";

        // Act
        Event event = repository.insertEvent(target, moment, subject);

        // Assert
        Assertions.assertNotNull(event);
        Assertions.assertEquals(target, event.getTarget());
        Assertions.assertEquals(moment, event.getMoment());
        Assertions.assertEquals(subject, event.getSubject());
    }

    @Test
    void insertEventWithReason() {
        // Arrange
        Observable target = trackRepository.getTracks().get(0);
        Timestamp moment = Timestamp.valueOf("2023-08-19 12:00:05");
        String subject = "break";
        String reason = "Snorlax sleepin on da track";

        // Act
        Event event = repository.insertEvent(target, moment, subject, reason);

        // Assert
        Assertions.assertNotNull(event);
        Assertions.assertEquals(target, event.getTarget());
        Assertions.assertEquals(moment, event.getMoment());
        Assertions.assertEquals(subject, event.getSubject());
        Assertions.assertEquals(reason, event.getReason());
    }

    @Test
    void insertLocalEvent() {
        // Arrange
        Observable target = trackRepository.getTracks().get(0);
        Timestamp moment = Timestamp.valueOf("2023-08-19 12:00:05");
        String subject = "break";
        double latitude = 45;
        double longitude = 70;

        // Act
        LocalEvent event = repository.insertLocalEvent(target, moment, subject, latitude, longitude);

        // Assert
        Assertions.assertNotNull(event);
        Assertions.assertEquals(target, event.getTarget());
        Assertions.assertEquals(moment, event.getMoment());
        Assertions.assertEquals(subject, event.getSubject());
    }

    @Test
    void insertLocalEventWithReason() {
        // Arrange
        Observable target = trackRepository.getTracks().get(0);
        Timestamp moment = Timestamp.valueOf("2023-08-19 12:00:05");
        String subject = "break";
        double latitude = 45;
        double longitude = 70;
        String reason = "Snorlax sleepin on da track";

        // Act
        LocalEvent event = repository.insertLocalEvent(target, moment, subject, latitude, longitude, reason);

        // Assert
        Assertions.assertNotNull(event);
        Assertions.assertEquals(target, event.getTarget());
        Assertions.assertEquals(moment, event.getMoment());
        Assertions.assertEquals(subject, event.getSubject());
        Assertions.assertEquals(reason, event.getReason());
    }
}
