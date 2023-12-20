package be.howest.ti.adria.logic.data.repositories;

import be.howest.ti.adria.logic.domain.Event;
import be.howest.ti.adria.logic.domain.LocalEvent;
import be.howest.ti.adria.logic.domain.Observable;

import java.sql.Timestamp;
import java.util.List;

public interface EventRepository {
    List<Event> getEvents();
    Event getEvent(int id);

    Event insertEvent(Observable target, Timestamp moment, String what);
    Event insertEvent(Observable target, Timestamp moment, String what, String reason);

    LocalEvent insertLocalEvent(Observable target, Timestamp moment, String what, double latitude, double longitude);
    LocalEvent insertLocalEvent(Observable target, Timestamp moment, String what, double latitude, double longitude, String reason);
}
