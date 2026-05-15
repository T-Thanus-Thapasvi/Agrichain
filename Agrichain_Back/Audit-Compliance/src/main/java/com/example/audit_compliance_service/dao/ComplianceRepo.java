package com.example.audit_compliance_service.dao;

import com.example.audit_compliance_service.entity.Compliance;
import com.example.audit_compliance_service.enums.ComplianceResult;
import com.example.audit_compliance_service.enums.ComplianceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplianceRepo extends JpaRepository<Compliance, Long> {
    List<Compliance> findByAuditId(Long auditId);
    List<Compliance> findByEntityId(Long entityId);
    List<Compliance> findByType(ComplianceType type);
    List<Compliance> findByResult(ComplianceResult result);
}
