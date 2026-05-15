package com.cts.report_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Performed by (Email/Username) cannot be blank")
    private String performedBy;

    @NotBlank(message = "Action cannot be blank")
    private String action;

    private String role;

    private String details;

    private LocalDateTime timestamp;
}