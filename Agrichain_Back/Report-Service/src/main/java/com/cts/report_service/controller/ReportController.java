package com.cts.report_service.controller;

import com.cts.report_service.client.AuditClient;
import com.cts.report_service.dto.AuditLogRequestDTO;
import com.cts.report_service.dto.ReportDTO;
import com.cts.report_service.entity.Report;
import com.cts.report_service.enums.TransactionStatus;
import com.cts.report_service.service.ReportService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private AuditClient auditClient;

    @Value("${jwt.secret}")
    String secret;

    private Claims getClaimsFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private void logToAudit(HttpServletRequest request, String action, String details) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null) {
                Claims claims = getClaimsFromToken(authHeader);

                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                AuditLogRequestDTO auditDTO = AuditLogRequestDTO.builder()
                        .performedBy(email)
                        .action(action)
                        .role(role != null ? role : "USER")
                        .details(details)
                        .timestamp(LocalDateTime.now())
                        .build();

                auditClient.createLog(auditDTO);
            }
        } catch (Exception e) {
            System.err.println("Audit Logging failed: " + e.getMessage());
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<Report> generateReport(@Valid @RequestBody ReportDTO reportDto, HttpServletRequest request) {
        // 1. Convert DTO to Entity (Manual or using a Mapper)
        Report report = new Report();
        report.setScope(reportDto.getScope());
        report.setMetrics(reportDto.getMetrics());
        report.setGeneratedDate(LocalDate.now());

        Report newReport = reportService.create(report);
        // Log action
        logToAudit(request, "CREATE_REPORT", "Created report with ID: " + newReport.getReportId());
        // 2. Save using Service
        return ResponseEntity.ok(newReport);
    }

    @GetMapping("/{id}")
    public Report getReport(@PathVariable Long id) {
        return reportService.findReport(id);
    }

    @GetMapping("/all")
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Report> updateReport(@PathVariable Long id, @Valid @RequestBody ReportDTO reportDto, HttpServletRequest request) {
        Report report = new Report();
        report.setScope(reportDto.getScope());
        report.setMetrics(reportDto.getMetrics());

        logToAudit(request, "UPDATE_REPORT", "Updated report ID: " + id);

        return ResponseEntity.ok(reportService.update(id, report));
    }

    @DeleteMapping("/delete/{id}")
    public String deleteReport(@PathVariable Long id, HttpServletRequest request) {
        reportService.delete(id);

        logToAudit(request, "DELETE_REPORT", "Deleted report ID: " + id);
        return "Deleted Successfully";
    }

    @GetMapping("/scope/{scope}")
    public List<Report> getByScope(@PathVariable String scope) {
        return reportService.getReportsByScope(scope);
    }

    @GetMapping("/range")
    public List<Report> getByRange(
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate start,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate end) {
        return reportService.findByGeneratedDateBetween(start, end);
    }

    @Transactional
    @GetMapping("/transactionsreport/{status}")
    public Report generateTransactionReport(@PathVariable String status) {
        return reportService.generateTransactionReport(status);
    }

    @Transactional
    @GetMapping("/transactionsreport/range")
    public Report generateTimedTransactionReport(
            @RequestParam String status,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate start,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate end
    ) {
        return reportService.generateTimedTransactionReport(status, start, end);
    }

}