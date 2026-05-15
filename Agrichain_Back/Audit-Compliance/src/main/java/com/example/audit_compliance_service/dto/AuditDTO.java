package com.example.audit_compliance_service.dto;

import com.example.audit_compliance_service.enums.AuditScope;
import com.example.audit_compliance_service.enums.AuditStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditDTO {

    private Long auditId;

    @NotNull
    private Long officerId;

    @NotNull
    private AuditScope scope;

    @NotBlank
    private String findings;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull
    private AuditStatus status;
}
