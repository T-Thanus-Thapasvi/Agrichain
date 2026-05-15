package com.cts.report_service.entity;

import com.cts.report_service.enums.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trans_seq")
    @SequenceGenerator(name = "trans_seq", sequenceName = "transaction_sequence", allocationSize = 1)
    private Long transactionId;

    private Long orderId;

    @Column(nullable = false)
    private double transactionAmount;

    @Column(nullable = false)
    private LocalDate transactionDate;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;
}