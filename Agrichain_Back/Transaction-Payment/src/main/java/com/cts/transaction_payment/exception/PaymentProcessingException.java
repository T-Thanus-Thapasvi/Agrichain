package com.cts.transaction_payment.exception;

public class PaymentProcessingException extends Exception {
    private String errorCode;

    public String getErrorCode() {
        return errorCode;
    }
}
