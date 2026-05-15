package com.cts.transaction_payment.dto.response;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {
    private Long transactionId;
    private Long orderId;
    private String status;
    private double finalAmount;
    private LocalDate date;
}
