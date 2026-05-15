package com.cts.transaction_payment.dto.request;

import com.cts.transaction_payment.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDTO {
    private Long orderId;
    private double amount;
    private TransactionStatus status;
}