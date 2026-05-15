package com.cts.report_service.dto;

import com.cts.report_service.enums.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionReportDTO {
    private double transactionAmount;
    private LocalDate transactionDate;
    private TransactionStatus transactionStatus;
}
