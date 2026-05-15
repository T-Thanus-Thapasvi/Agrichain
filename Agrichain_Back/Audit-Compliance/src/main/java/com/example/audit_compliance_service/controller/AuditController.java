package com.example.audit_compliance_service.controller;

import com.example.audit_compliance_service.dto.AuditDTO;
import com.example.audit_compliance_service.enums.AuditScope;
import com.example.audit_compliance_service.enums.AuditStatus;
import com.example.audit_compliance_service.service.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audits")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping("/create")
    public ResponseEntity<AuditDTO> createAudit(@RequestBody AuditDTO auditDto) {
        return ResponseEntity.ok(auditService.createAudit(auditDto));
    }

    @GetMapping("/all")
    public List<AuditDTO> getAllAudits() {
        return auditService.getAllAudits();
    }

    @GetMapping("/{id}")
    public AuditDTO getAuditById(@PathVariable Long id) {
        return auditService.findAuditById(id);
    }

    @PutMapping("/{id}")
    public AuditDTO updateAudit(@PathVariable Long id, @RequestBody AuditDTO auditDto) {
        return auditService.updateAudit(id, auditDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAudit(@PathVariable Long id) {
        auditService.deleteAudit(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/officer/{officerId}")
    public List<AuditDTO> getAuditsByOfficer(@PathVariable Long officerId) {
        return auditService.getAuditsByOfficer(officerId);
    }

    @GetMapping("/scope/{scope}")
    public List<AuditDTO> getAuditsByScope(@PathVariable AuditScope scope) {
        return auditService.getAuditsByScope(scope);
    }

    @GetMapping("/status/{status}")
    public List<AuditDTO> getAuditsByStatus(@PathVariable AuditStatus status) {
        return auditService.getAuditsByStatus(status);
    }
}
