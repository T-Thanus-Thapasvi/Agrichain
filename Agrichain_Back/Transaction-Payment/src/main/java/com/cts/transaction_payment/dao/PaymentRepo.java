
package com.cts.transaction_payment.dao;

import com.cts.transaction_payment.entity.Payment;
import com.cts.transaction_payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {


    List<Payment> findByPaymentStatus(PaymentStatus status);

    List<Payment> findByMethod(String method);

    Optional<Payment> findByTransaction_TransactionId(Long transactionId);

    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.paymentStatus = :status WHERE p.paymentId = :id")
    int updatePaymentStatus(@Param("id") Long id, @Param("status") PaymentStatus status);
}