package com.cts.transaction_payment.controller;
import com.cts.transaction_payment.entity.Payment;
import com.cts.transaction_payment.enums.PaymentStatus;
import com.cts.transaction_payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;


    @PostMapping("/record")
    public ResponseEntity<Payment> recordPayment(@RequestBody Payment payment) {
        Payment recordedPayment = paymentService.recordPayment(payment);
        return ResponseEntity.ok(paymentService.recordPayment(payment));
    }


    @PutMapping("/confirm/{paymentId}")
    public ResponseEntity<Payment> confirmPayment(
            @PathVariable Long paymentId,
            @RequestParam PaymentStatus status) {

        // This service method now handles the logic to reduce CropListing quantity
        Payment updatedPayment = paymentService.processPaymentCallback(paymentId, status);
        return ResponseEntity.ok(updatedPayment);
    }


    @GetMapping("/filter")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@RequestParam String status) {
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status));
    }
}