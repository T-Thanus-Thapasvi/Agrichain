package com.cts.notification_service.dto;

import com.cts.notification_service.enums.NotificationCategory;
import com.cts.notification_service.enums.NotificationStatus;
import com.cts.notification_service.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Long userId; //  Only for automated message (receiver) eg: officer accepted something
    private UserRole role; // can be none for automated notifications for single user

    @NotBlank(message = "Subject is needed")
    private String subject;

    @NotBlank(message = "Message content is needed")
    private String message;

    @NotNull(message = "Message category is needed")
    private NotificationCategory category;

    private NotificationStatus status; // Only works for automated notifications for single user
}
