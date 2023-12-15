package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.domain.Event;
import be.howest.ti.adria.logic.domain.Observable;
import be.howest.ti.adria.logic.domain.Station;
import be.howest.ti.adria.logic.domain.Track;
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
    void insertEvent() {
        // Arrange
        Observable target = trackRepository.getTracks().get(0);
        Timestamp moment = new Timestamp(2023, 8, 19, 12, 0, 5, 0);
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
        Timestamp moment = new Timestamp(2023, 8, 19, 12, 0, 5, 0);
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
}
