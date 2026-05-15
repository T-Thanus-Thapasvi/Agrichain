package com.cts.transaction_payment.dto;


import com.cts.transaction_payment.enums.TransactionStatus;
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
