package com.cts.croplisting_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String action; // e.g., "APPROVED", "REJECTED"
    private String targetType; // e.g., "CROP", "DOCUMENT"
    private Long targetId;
    private String reason;
    private LocalDateTime timestamp;
}
