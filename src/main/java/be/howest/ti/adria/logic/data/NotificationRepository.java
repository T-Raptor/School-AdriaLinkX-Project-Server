package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.domain.Notification;

import java.util.List;

public interface NotificationRepository {
    List<Notification> getNotifications();
    Notification insertNotification(int event, String company);
    Notification updateNotification(int event, String company, boolean read);
}
