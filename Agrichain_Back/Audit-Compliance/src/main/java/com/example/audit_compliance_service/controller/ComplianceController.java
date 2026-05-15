package com.example.audit_compliance_service.controller;

import com.example.audit_compliance_service.dto.ComplianceDTO;
import com.example.audit_compliance_service.service.ComplianceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compliances")
public class ComplianceController {

    private final ComplianceService complianceAuditService;

    public ComplianceController(ComplianceService complianceAuditService) {
        this.complianceAuditService = complianceAuditService;
    }

    // ✅ auditId comes from path variable, not required in JSON body
    @PostMapping("/create/{auditId}")
    public ResponseEntity<ComplianceDTO> addComplianceToAudit(
            @PathVariable Long auditId,
            @Valid @RequestBody ComplianceDTO complianceDto) {
        // set auditId into DTO before saving
        complianceDto.setAuditId(auditId);
        ComplianceDTO saved = complianceAuditService.addComplianceToAudit(auditId, complianceDto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/all")
    public List<ComplianceDTO> getAllCompliances() {
        return complianceAuditService.getAllCompliances();
    }

    @GetMapping("/{id}")
    public ComplianceDTO getComplianceById(@PathVariable Long id) {
        return complianceAuditService.findComplianceById(id);
    }

    @PutMapping("/update/{id}")
    public ComplianceDTO updateCompliance(@PathVariable Long id,
                                          @Valid @RequestBody ComplianceDTO complianceDto) {
        return complianceAuditService.updateCompliance(id, complianceDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCompliance(@PathVariable Long id) {
        complianceAuditService.deleteCompliance(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/audit/{auditId}")
    public List<ComplianceDTO> getCompliancesByAudit(@PathVariable Long auditId) {
        return complianceAuditService.getCompliancesByAudit(auditId);
    }
}
