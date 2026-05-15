package com.cts.transaction_payment.dto.request;

import com.cts.transaction_payment.enums.PaymentType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentRequestDTO {
    private Long transactionId;
    private PaymentType method;
}