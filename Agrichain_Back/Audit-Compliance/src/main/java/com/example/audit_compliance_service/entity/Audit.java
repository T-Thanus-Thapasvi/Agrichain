package com.example.audit_compliance_service.entity;

import com.example.audit_compliance_service.enums.AuditScope;
import com.example.audit_compliance_service.enums.AuditStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit")
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    private Long officerId;

    @Enumerated(EnumType.STRING)
    private AuditScope scope;

    private String findings;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private AuditStatus status;
}
