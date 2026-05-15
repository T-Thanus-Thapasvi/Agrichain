package com.example.audit_compliance_service.dao;

import com.example.audit_compliance_service.entity.Audit;
import com.example.audit_compliance_service.enums.AuditScope;
import com.example.audit_compliance_service.enums.AuditStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditRepo extends JpaRepository<Audit, Long> {
    List<Audit> findByOfficerId(Long officerId);
    List<Audit> findByScope(AuditScope scope);
    List<Audit> findByStatus(AuditStatus status);
}
