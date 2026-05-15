package com.cts.transaction_payment.entity;

import com.cts.transaction_payment.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trans_seq")
    @SequenceGenerator(name = "trans_seq", sequenceName = "transaction_sequence", allocationSize = 1)
    private Long transactionId;

    private Long orderId; // Changed from Order object to ID

    private double transactionAmount;
    private LocalDate transactionDate;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;
}

