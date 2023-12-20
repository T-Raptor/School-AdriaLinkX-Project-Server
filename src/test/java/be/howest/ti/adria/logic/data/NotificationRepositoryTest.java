package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.data.repositories.NotificationRepository;
import be.howest.ti.adria.logic.domain.Notification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public abstract class NotificationRepositoryTest {
    protected NotificationRepository repository;

    @Test
    void getNotifications() {
        // Arrange

        // Act
        List<Notification> notifications = repository.getNotifications();

        // Assert
        Assertions.assertNotNull(notifications);
        Assertions.assertFalse(notifications.isEmpty());
    }

    @Test
    void insertNotification() {
        // Arrange
        int event = 1;
        String company = "Mesa";

        // Act
        Notification notification = repository.insertNotification(event, company);

        // Assert
        Assertions.assertNotNull(notification);
        Assertions.assertEquals(company, notification.getCompany());
        Assertions.assertFalse(notification.isRead());

        Assertions.assertNotNull(notification.getEvent());
        Assertions.assertEquals(event, notification.getEvent().getId());
    }

    @Test
    void updateNotification() {
        // Arrange
        int event = 1;
        String company = "Mesa";
        repository.insertNotification(event, company);

        // Act
        Notification notification = repository.updateNotification(event, company, true);

        // Assert
        Assertions.assertNotNull(notification);
        Assertions.assertTrue(notification.isRead());
    }
}
