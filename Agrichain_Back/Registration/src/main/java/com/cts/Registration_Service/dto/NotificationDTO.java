package com.cts.Registration_Service.dto;

import com.cts.Registration_Service.enums.NotificationCategory;
import com.cts.Registration_Service.enums.NotificationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    @NotNull(message = "Need receiver user")
    private Long userId;

    @NotBlank(message = "Subject is needed")
    private String subject;

    @NotBlank(message = "Message content is needed")
    private String message;

    @NotNull(message = "Message category is needed")
    private NotificationCategory category;

    private NotificationStatus status;
}
