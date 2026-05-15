package com.cts.notification_service.dao;

import com.cts.notification_service.entity.Notification;
import com.cts.notification_service.enums.UserRole;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepo extends JpaRepository<Notification, Long>  {
    List<Notification> findNotificationByRole(UserRole role);
}
