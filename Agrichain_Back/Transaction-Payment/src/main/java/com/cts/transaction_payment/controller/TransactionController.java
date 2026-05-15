package com.cts.transaction_payment.controller;



import com.cts.transaction_payment.dto.request.PaymentRequestDTO;
import com.cts.transaction_payment.dto.request.TransactionRequestDTO;
import com.cts.transaction_payment.entity.Payment;
import com.cts.transaction_payment.entity.Transaction;
import com.cts.transaction_payment.enums.PaymentStatus;
import com.cts.transaction_payment.enums.TransactionStatus;
import com.cts.transaction_payment.service.PaymentService;
import com.cts.transaction_payment.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/test-data")
    public ResponseEntity<String> getHardcodedTransaction() {
        System.out.println("DEBUG: Hardcoded test-data method was called!");

        // This will show exactly "Yes Working" on your screen
        return new ResponseEntity<>("Yes Working", HttpStatus.OK);
    }

    @PostMapping("/initiate")
    public ResponseEntity<Transaction> initiateTransaction(@RequestBody TransactionRequestDTO request) {
        // 1. Create the Transaction entity
        Transaction transaction = new Transaction();


        transaction.setOrderId(request.getOrderId());

        // 3. Set the rest of the fields from the request
        transaction.setTransactionAmount(request.getAmount());
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        transaction.setTransactionDate(LocalDate.now());

        // 4. Save via service
        Transaction createdTx = transactionService.createTransaction(transaction);
        return new ResponseEntity<>(createdTx, HttpStatus.CREATED);
    }

    @PostMapping("/payment/record")
    public ResponseEntity<Payment> recordPayment(@RequestBody PaymentRequestDTO request) {
        // 1. Create a new Payment entity
        Payment payment = new Payment();

        // 2. Link the transaction (Service will fetch the full object)
        Transaction tx = new Transaction();
        tx.setTransactionId(request.getTransactionId());
        payment.setTransaction(tx);

        // 3. Set the method
        payment.setMethod(request.getMethod());

        // 4. Delegate to service
        Payment recordedPayment = paymentService.recordPayment(payment);
        return new ResponseEntity<>(recordedPayment, HttpStatus.CREATED);
    }

    @PutMapping("/payment/callback/{paymentId}")
    public ResponseEntity<Payment> handlePaymentCallback(
            @PathVariable Long paymentId,
            @RequestParam PaymentStatus status) {

        Payment updatedPayment = paymentService.processPaymentCallback(paymentId, status);
        return ResponseEntity.ok(updatedPayment);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Transaction>> getTransactionsByStatus(@PathVariable String status) {
        TransactionStatus transactionStatus = TransactionStatus.valueOf(status.toUpperCase());
        List<Transaction> transactions = transactionService.getTransactionsByStatus(transactionStatus);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Transaction>> getTransactionsByFilter(
            @RequestParam String status,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate start,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate end) {

        List<Transaction> transactions = transactionService.getTransactionsByFilter(status, start, end);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}/final-amount")
    public ResponseEntity<Double> getFinalAmount(@PathVariable Long transactionId) {
        Double finalAmount = transactionService.calculateFinalAmount(transactionId);
        return ResponseEntity.ok(finalAmount);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Transaction> updateTransactionStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Transaction updatedTx = transactionService.updateTransactionStatus(id, status);
        return ResponseEntity.ok(updatedTx);
    }

    @PutMapping("/{id}/finalize")
    public ResponseEntity<Transaction> finalizeTransaction(@PathVariable Long id) {
        Transaction finalizedTx = transactionService.finalizeTransaction(id);
        return ResponseEntity.ok(finalizedTx);
    }
}