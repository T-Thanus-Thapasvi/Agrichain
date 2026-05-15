package com.example.audit_compliance_service.service;

import com.example.audit_compliance_service.dao.ComplianceRepo;
import com.example.audit_compliance_service.dto.ComplianceDTO;
import com.example.audit_compliance_service.entity.Compliance;
import com.example.audit_compliance_service.enums.ComplianceResult;
import com.example.audit_compliance_service.enums.ComplianceType;
import com.example.audit_compliance_service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplianceService {

    private final ComplianceRepo complianceRepo;

    public ComplianceService(ComplianceRepo complianceRepo) {
        this.complianceRepo = complianceRepo;
    }

    // Add compliance linked to an audit
    public ComplianceDTO addComplianceToAudit(Long auditId, ComplianceDTO dto) {
        Compliance compliance = new Compliance();
        compliance.setAuditId(auditId);   // <-- link to audit
        compliance.setEntityId(dto.getEntityId());
        compliance.setType(dto.getType());
        compliance.setResult(dto.getResult());
        compliance.setDate(dto.getDate());
        compliance.setNotes(dto.getNotes());
        return toComplianceDTO(complianceRepo.save(compliance));
    }

    public ComplianceDTO findComplianceById(Long id) {
        return complianceRepo.findById(id)
                .map(this::toComplianceDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance not found with ID: " + id));
    }

    public List<ComplianceDTO> getAllCompliances() {
        return complianceRepo.findAll().stream().map(this::toComplianceDTO).toList();
    }

    public ComplianceDTO updateCompliance(Long id, ComplianceDTO updatedDto) {
        return complianceRepo.findById(id).map(compliance -> {
            compliance.setEntityId(updatedDto.getEntityId());
            compliance.setType(updatedDto.getType());
            compliance.setResult(updatedDto.getResult());
            compliance.setDate(updatedDto.getDate());
            compliance.setNotes(updatedDto.getNotes());
            return toComplianceDTO(complianceRepo.save(compliance));
        }).orElseThrow(() -> new ResourceNotFoundException("Compliance not found with ID: " + id));
    }

    public void deleteCompliance(Long id) {
        complianceRepo.deleteById(id);
    }

    public List<ComplianceDTO> getCompliancesByAudit(Long auditId) {
        return complianceRepo.findByAuditId(auditId).stream().map(this::toComplianceDTO).toList();
    }

    public List<ComplianceDTO> getCompliancesByEntity(Long entityId) {
        return complianceRepo.findByEntityId(entityId).stream().map(this::toComplianceDTO).toList();
    }

    public List<ComplianceDTO> getCompliancesByType(ComplianceType type) {
        return complianceRepo.findByType(type).stream().map(this::toComplianceDTO).toList();
    }

    public List<ComplianceDTO> getCompliancesByResult(ComplianceResult result) {
        return complianceRepo.findByResult(result).stream().map(this::toComplianceDTO).toList();
    }

    private ComplianceDTO toComplianceDTO(Compliance compliance) {
        ComplianceDTO dto = new ComplianceDTO();
        dto.setComplianceId(compliance.getComplianceId());
        dto.setAuditId(compliance.getAuditId());   // <-- include audit link
        dto.setEntityId(compliance.getEntityId());
        dto.setType(compliance.getType());
        dto.setResult(compliance.getResult());
        dto.setDate(compliance.getDate());
        dto.setNotes(compliance.getNotes());
        return dto;
    }
}
