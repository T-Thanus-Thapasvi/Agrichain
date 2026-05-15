package com.cts.transaction_payment.dao;

import com.cts.transaction_payment.entity.Transaction;
import com.cts.transaction_payment.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findByTransactionStatus(TransactionStatus transactionStatus);

    // CORRECT
    List<Transaction> findByOrderId(Long orderId);

    List<Transaction> findByTransactionStatusAndTransactionDateBetween(TransactionStatus transactionStatus, LocalDate start, LocalDate end);

    @Modifying
    @Transactional
    @Query("UPDATE Transaction t SET t.transactionStatus = :status WHERE t.transactionId = :id")
    int updateTransactionStatus(@Param("id") int id, @Param("status") TransactionStatus status);
}