package com.example.audit_compliance_service.dto;

import com.example.audit_compliance_service.enums.ComplianceResult;
import com.example.audit_compliance_service.enums.ComplianceType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceDTO {

    private Long complianceId;

    private Long auditId;   // <-- added

    @NotNull
    private Long entityId;

    @NotNull
    private ComplianceType type;

    @NotNull
    private ComplianceResult result;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String notes;
}
