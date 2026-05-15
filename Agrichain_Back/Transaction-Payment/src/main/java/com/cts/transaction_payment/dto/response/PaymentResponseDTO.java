package com.cts.transaction_payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long paymentId;
    private Long transactionId;
    private String paymentStatus;
    private String method;
    private LocalDate paymentDate;
}
