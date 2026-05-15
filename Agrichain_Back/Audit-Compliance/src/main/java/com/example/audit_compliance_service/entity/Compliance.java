package com.example.audit_compliance_service.entity;

import com.example.audit_compliance_service.enums.ComplianceResult;
import com.example.audit_compliance_service.enums.ComplianceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "compliance")
public class Compliance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complianceId;

    // Link to Audit
    private Long auditId;

    private Long entityId;

    @Enumerated(EnumType.STRING)
    private ComplianceType type;

    @Enumerated(EnumType.STRING)
    private ComplianceResult result;

    private LocalDate date;

    private String notes;
}
