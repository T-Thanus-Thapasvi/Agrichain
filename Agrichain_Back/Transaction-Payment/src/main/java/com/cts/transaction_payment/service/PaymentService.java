package com.cts.transaction_payment.service;

import com.cts.transaction_payment.dao.PaymentRepo;
import com.cts.transaction_payment.dao.TransactionRepo;
import com.cts.transaction_payment.entity.Payment;
import com.cts.transaction_payment.entity.Transaction;
import com.cts.transaction_payment.enums.PaymentStatus;
import com.cts.transaction_payment.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepo transactionRepo;

    @Transactional
    public Payment recordPayment(Payment payment) {
        Transaction tx = transactionRepo.findById(payment.getTransaction().getTransactionId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        payment.setTransaction(tx);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentStatus(PaymentStatus.PAID);

        Payment savedPayment = paymentRepo.save(payment);

        // ADD THIS LINE TO TRIGGER THE REDUCTION
        System.out.println(">>> Payment recorded. Triggering finalization...");
        transactionService.finalizeTransaction(tx.getTransactionId());

        return savedPayment;
    }

    @Transactional
    public Payment processPaymentCallback(Long paymentId, PaymentStatus newStatus) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found"));

        payment.setPaymentStatus(newStatus);

        if (newStatus == PaymentStatus.PAID) {
            transactionService.finalizeTransaction(payment.getTransaction().getTransactionId());
        }
        else if (newStatus == PaymentStatus.FAILED) {
            transactionService.cancelTransaction(payment.getTransaction().getTransactionId());
        }

        return paymentRepo.save(payment);
    }

    public String generatePaymentReference(int transactionId) {
        return "AGRI-" + System.currentTimeMillis() + "-" + transactionId;
    }

    public boolean verifyWithGateway(String gatewayId) {
        return gatewayId != null && !gatewayId.isEmpty();
    }



    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepo.findByPaymentStatus(PaymentStatus.valueOf(status.toUpperCase()));
    }
}
