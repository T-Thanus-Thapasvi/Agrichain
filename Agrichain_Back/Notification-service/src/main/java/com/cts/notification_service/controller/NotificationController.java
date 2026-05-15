package com.cts.notification_service.controller;

import com.cts.notification_service.client.AuditClient;
import com.cts.notification_service.dto.AuditLogRequestDTO;
import com.cts.notification_service.dto.NotificationDTO;
import com.cts.notification_service.entity.Notification;
import com.cts.notification_service.enums.NotificationStatus;
import com.cts.notification_service.enums.UserRole;
import com.cts.notification_service.service.NotificationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuditClient auditClient;

    @Value("${jwt.secret}")
    String secret;

    private Claims getClaimsFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private void logToAudit(HttpServletRequest request, String action, String details) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null) {
                Claims claims = getClaimsFromToken(authHeader);

                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                AuditLogRequestDTO auditDTO = AuditLogRequestDTO.builder()
                        .performedBy(email)
                        .action(action)
                        .role(role != null ? role : "USER")
                        .details(details)
                        .timestamp(LocalDateTime.now())
                        .build();

                auditClient.createLog(auditDTO);
            }
        } catch (Exception e) {
            System.err.println("Audit Logging failed: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Notification> createNotification(@Valid @RequestBody NotificationDTO notificationDTO, HttpServletRequest request) {
        Notification notification = new Notification();
        if(notificationDTO.getUserId() != null && notificationDTO.getRole() == null) {
            notification.setUserId(notificationDTO.getUserId());
        }
        if(notificationDTO.getRole() != null){
            notification.setRole(notificationDTO.getRole());
        }
        notification.setSubject(notificationDTO.getSubject());
        notification.setMessage(notificationDTO.getMessage());
        notification.setCategory(notificationDTO.getCategory());
        if(notificationDTO.getUserId() != null && notificationDTO.getRole() == null){
            notification.setStatus(NotificationStatus.UNREAD);
        }
        notification.setCreatedDate(LocalDate.now());

        Notification newNotification = notificationService.create(notification);
        logToAudit(request, "CREATE_NOTIFICATION", "Created Notification with ID: " + newNotification.getNotificationID());

        return ResponseEntity.ok(newNotification);
    }

    @GetMapping("/{id}")
    public Notification getNotification(@PathVariable Long id) {
        return notificationService.findById(id);
    }

    @GetMapping("/all")
    public List<Notification> getAllNotifications() {
        return notificationService.fetchAll();
    }

    @DeleteMapping("/delete/{id}")
    public void deleteNotification(@PathVariable Long id, HttpServletRequest request) {
        logToAudit(request, "DELETE_REPORT", "Deleted Notification ID: " + id);
        notificationService.delete(id);
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<Notification> updateNotificationStatusById(@PathVariable Long id) {
        Notification updatedNotification = notificationService.updateStatusById(id);
        return ResponseEntity.ok(updatedNotification);
    }

    @GetMapping("/role/{role}")
    public List<Notification> getByScope(@PathVariable UserRole role) {
        return notificationService.findNotificationByRole(role);
    }
}
