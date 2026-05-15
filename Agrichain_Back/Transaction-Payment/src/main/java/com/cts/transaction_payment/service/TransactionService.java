package com.cts.transaction_payment.service;

import com.cts.transaction_payment.client.MarketClient;
import com.cts.transaction_payment.dao.TransactionRepo;
import com.cts.transaction_payment.dto.OrderDTO;
import com.cts.transaction_payment.entity.Transaction;
import com.cts.transaction_payment.enums.TransactionStatus;
import com.cts.transaction_payment.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private MarketClient marketClient; // Bridge to the Market/Crop Microservice

    /**
     * Retrieves transactions based on their status.
     * Functionality preserved from original combined project.
     */
    public List<Transaction> getTransactionsByStatus(TransactionStatus status) {
        try {
            return transactionRepo.findByTransactionStatus(status);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid transaction status: " + status);
        }
    }

    /**
     * Calculates the final amount including a platform fee.
     */
    public Double calculateFinalAmount(Long transactionId) {
        Transaction tx = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        // Standard 2% platform fee logic preserved
        return tx.getTransactionAmount() * 1.02;
    }

    /**
     * Initiates a new transaction.
     * Now validates existence of the Order via Feign Client instead of direct DB.
     */
    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        // 1. Verify the order exists in the Market Microservice
        // In microservices, we check if the remote resource is valid
        Object order = marketClient.getOrderById(transaction.getOrderId());
        if (order == null) {
            throw new ResourceNotFoundException("Order not found with ID: " + transaction.getOrderId());
        }

        // 2. Set defaults and save locally
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        transaction.setTransactionDate(LocalDate.now());

        return transactionRepo.save(transaction);
    }

    /**
     * Finalizes the transaction upon successful payment.
     * Triggers the remote Crop service to reduce inventory quantity.
     */

    @Transactional
    public Transaction finalizeTransaction(Long transactionId) {
        // 1. Fetch the local transaction record
        Transaction tx = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));

        // Prevent re-processing
        if (tx.getTransactionStatus() == TransactionStatus.COMPLETED) {
            return tx;
        }

        try {
            // 2. Get Order details
            OrderDTO order = marketClient.getOrderById(tx.getOrderId());

            System.out.println(">>> STARTING FINALIZATION FOR ORDER: " + order.getOrderId());

            // 3. STEP ONE: Reduce Quantity (Database Math)
            // We do this first so the database updates the "truth"
            System.out.println("DEBUG: Executing Inventory Reduction...");
            marketClient.reduceQuantity(order.getListingId(), order.getQuantity());

            // 4. STEP TWO: Update Order Status
            // Because we fixed updateOrderStatus to use a Query, it won't overwrite the reduction
            System.out.println("DEBUG: Updating Order Status to COMPLETED...");
            marketClient.updateOrderStatus(tx.getOrderId(), "COMPLETED");

            // 5. Update Local Transaction
            tx.setTransactionStatus(TransactionStatus.COMPLETED);
            Transaction updatedTx = transactionRepo.save(tx);

            System.out.println(">>> SUCCESS: Transaction " + transactionId + " finalized. Quantity reduced.");
            return updatedTx;

        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: Finalization failed. Rolling back transaction.");
            // Since this is @Transactional, if reduceQuantity worked but updateOrderStatus failed,
            // it would technically be hard to rollback remote calls,
            // but this setup is the safest for simple microservices.
            throw new RuntimeException("Finalization failed: " + e.getMessage());
        }
    }

    /**
     * Cancels a transaction and notifies the Market service.
     */
    @Transactional
    public void cancelTransaction(Long transactionId) {
        // 1. Fetch the transaction
        Transaction tx = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        // 2. Update local status
        tx.setTransactionStatus(TransactionStatus.CANCELLED);

        // 3. Update the associated remote order status
        marketClient.updateOrderStatus(tx.getOrderId(), "CANCELLED");

        // 4. Save changes
        transactionRepo.save(tx);
    }

    /**
     * Filters transactions by status and date range.
     */
    public List<Transaction> getTransactionsByFilter(String status, LocalDate start, LocalDate end) {
        TransactionStatus transactionStatus = TransactionStatus.valueOf(status.toUpperCase());
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        return transactionRepo.findByTransactionStatusAndTransactionDateBetween(transactionStatus, start, end);
    }

    /**
     * Updates the status of a specific transaction.
     */
    @Transactional
    public Transaction updateTransactionStatus(Long id, String status) {
        // 1. Fetch from local DB
        Transaction tx = transactionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        try {
            // 2. Convert and Update
            TransactionStatus newStatus = TransactionStatus.valueOf(status.toUpperCase());
            tx.setTransactionStatus(newStatus);

            return transactionRepo.save(tx);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status +
                    ". Please use PENDING, COMPLETED, or CANCELLED.");
        }
    }
}