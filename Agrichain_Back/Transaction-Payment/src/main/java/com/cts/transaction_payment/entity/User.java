package com.cts.transaction_payment.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String email; // Used as username
    private String password;
    private String role; // e.g., TRADER, FARMER, ADMIN
}
