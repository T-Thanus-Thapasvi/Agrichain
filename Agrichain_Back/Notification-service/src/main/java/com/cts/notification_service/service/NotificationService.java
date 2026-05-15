package com.cts.notification_service.service;

import com.cts.notification_service.dao.NotificationRepo;
import com.cts.notification_service.entity.Notification;
import com.cts.notification_service.enums.NotificationStatus;
import com.cts.notification_service.enums.UserRole;
import com.cts.notification_service.exception.BadRequestException;
import com.cts.notification_service.exception.ResourceNotFoundException;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepo notificationRepo;

    public Notification findById(Long id) {
        return notificationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
    }

    public Notification create(Notification notification) {
        return notificationRepo.save(notification);
    }

    public List<Notification> fetchAll() {
        return notificationRepo.findAll();
    }

    public void delete(Long id) {
        if (!notificationRepo.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Notification not found with id " + id);
        }
        notificationRepo.deleteById(id);
    }

    public Notification updateStatusById(Long id) {
        Notification notification = notificationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id " + id));

        if(notification.getUserId() == null) {
            throw new BadRequestException("User id is null | This is not a automated Notification");
        } else {
            notification.setStatus(NotificationStatus.READ);
            return notificationRepo.save(notification);
        }
    }

    public List<Notification> findNotificationByRole(UserRole role) {
        return notificationRepo.findNotificationByRole(role);
    }
}
