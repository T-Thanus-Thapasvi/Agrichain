package com.cts.report_service.service;

import com.cts.report_service.client.TransactionClient;
import com.cts.report_service.dao.ReportRepo;
import com.cts.report_service.dto.TransactionReportDTO;
import com.cts.report_service.entity.Report;
import com.cts.report_service.exception.NullDataException;
import com.cts.report_service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private ReportRepo reportRepo;

    @Autowired
    private TransactionClient transactionClient;

    public Report create(Report report) {
        return reportRepo.save(report);
    }

    public Report findReport(Long id) {
        return reportRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + id));
    }

    public List<Report> getAllReports() {
        return reportRepo.findAll();
    }

    public Report update(Long id, Report updatedReport) {
        return reportRepo.findById(id).map(report -> {
            report.setScope(updatedReport.getScope());
            report.setMetrics(updatedReport.getMetrics());

            return reportRepo.save(report);
        }).orElseThrow(() -> new ResourceNotFoundException("Can't Update Report not found with id: " + id));
    }

    public void delete(Long id) {
        if (!reportRepo.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Report not found with id " + id);
        }
        reportRepo.deleteById(id);
    }


    public List<Report> findByGeneratedDateBetween(LocalDate start, LocalDate end) {
        return reportRepo.findByGeneratedDateBetween(start, end);
    }

    public List<Report> getReportsByScope(String scope) {
        return reportRepo.findByScope(scope);
    }

    public Report generateTransactionReport(String status) {

        List<TransactionReportDTO> transactions = transactionClient.getTransactionsByStatus(status);
        double totalAmount = 0;
        if (transactions == null || transactions.isEmpty()) {
            throw new NullDataException("Transactions not found");
        }
        for (TransactionReportDTO tx : transactions) {
            totalAmount += tx.getTransactionAmount();
        }

        Report report = new Report();
        report.setScope("Transactions - " + status);
        report.setMetrics("Amount:" + totalAmount + ",Total:" + transactions.size());
        report.setGeneratedDate(LocalDate.now());

        return reportRepo.save(report);
    }

    public Report generateTimedTransactionReport(String status, LocalDate start, LocalDate end) {

        List<TransactionReportDTO> transactions = transactionClient.getFilteredTransactions(
                status, start, end);

        if (transactions == null || transactions.isEmpty()) {
            throw new NullDataException("No transactions found for the given filters.");
        }

        double totalAmount = transactions.stream()
                .mapToDouble(TransactionReportDTO::getTransactionAmount)
                .sum();

        Report report = new Report();
        String timePeriod = calculateTimePeriod(start, end);
        report.setScope("Transactions - " + status + " - " + timePeriod);
        report.setMetrics("Amount:" + totalAmount + ",Total:" + transactions.size());
        report.setGeneratedDate(LocalDate.now());

        return reportRepo.save(report);
    }

    private String calculateTimePeriod(LocalDate start, LocalDate end) {
        int monthsSpanned = (end.getYear() - start.getYear()) * 12 + end.getMonthValue() - start.getMonthValue() + 1;
        String timePeriod;
        if (monthsSpanned == 3) {
            timePeriod = "Quarterly";
        } else if (monthsSpanned == 6) {
            timePeriod = "Half Yearly";
        } else if (monthsSpanned == 12) {
            timePeriod = "Yearly";
        } else {
            timePeriod = "Custom";
        }

        return timePeriod;
    }
}