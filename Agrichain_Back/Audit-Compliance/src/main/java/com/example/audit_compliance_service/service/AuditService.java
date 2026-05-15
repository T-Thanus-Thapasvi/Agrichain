package com.example.audit_compliance_service.service;

import com.example.audit_compliance_service.dao.AuditRepo;
import com.example.audit_compliance_service.dto.AuditDTO;
import com.example.audit_compliance_service.entity.Audit;
import com.example.audit_compliance_service.enums.AuditScope;
import com.example.audit_compliance_service.enums.AuditStatus;
import com.example.audit_compliance_service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {

    private final AuditRepo auditRepo;

    public AuditService(AuditRepo auditRepo) {
        this.auditRepo = auditRepo;
    }

    // Create
    public AuditDTO createAudit(AuditDTO dto) {
        Audit audit = new Audit();
        audit.setOfficerId(dto.getOfficerId());
        audit.setScope(dto.getScope());
        audit.setFindings(dto.getFindings());
        audit.setDate(dto.getDate());
        audit.setStatus(dto.getStatus());
        return toAuditDTO(auditRepo.save(audit));
    }

    // Read by ID
    public AuditDTO findAuditById(Long id) {
        return auditRepo.findById(id)
                .map(this::toAuditDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Audit not found with ID: " + id));
    }

    // Read all
    public List<AuditDTO> getAllAudits() {
        return auditRepo.findAll().stream().map(this::toAuditDTO).toList();
    }

    // Update
    public AuditDTO updateAudit(Long id, AuditDTO updatedDto) {
        return auditRepo.findById(id).map(audit -> {
            audit.setOfficerId(updatedDto.getOfficerId());
            audit.setScope(updatedDto.getScope());
            audit.setFindings(updatedDto.getFindings());
            audit.setDate(updatedDto.getDate());
            audit.setStatus(updatedDto.getStatus());
            return toAuditDTO(auditRepo.save(audit));
        }).orElseThrow(() -> new ResourceNotFoundException("Audit not found with ID: " + id));
    }

    // Delete
    public void deleteAudit(Long id) {
        auditRepo.deleteById(id);
    }

    // Queries
    public List<AuditDTO> getAuditsByOfficer(Long officerId) {
        return auditRepo.findByOfficerId(officerId).stream().map(this::toAuditDTO).toList();
    }

    public List<AuditDTO> getAuditsByScope(AuditScope scope) {
        return auditRepo.findByScope(scope).stream().map(this::toAuditDTO).toList();
    }

    public List<AuditDTO> getAuditsByStatus(AuditStatus status) {
        return auditRepo.findByStatus(status).stream().map(this::toAuditDTO).toList();
    }

    // Mapper
    private AuditDTO toAuditDTO(Audit audit) {
        AuditDTO dto = new AuditDTO();
        dto.setAuditId(audit.getAuditId());
        dto.setOfficerId(audit.getOfficerId());
        dto.setScope(audit.getScope());
        dto.setFindings(audit.getFindings());
        dto.setDate(audit.getDate());
        dto.setStatus(audit.getStatus());
        return dto;
    }
}
