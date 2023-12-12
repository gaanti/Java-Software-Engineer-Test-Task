package com.example.demo.domains.users.Student.accounts;

import com.example.demo.BaseEntity;
import com.example.demo.security.user.JwtUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction extends BaseEntity {

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp date;

    @OneToOne
    @JsonIncludeProperties(value = {"id", "firstName", "lastName"})
    @JoinColumn(updatable = false, nullable = false)
    private JwtUser accepter;

    private float value;

    @Column(columnDefinition = "VARCHAR(255)")
    String description;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('Cash', 'Check', 'MC', 'Visa', 'Discover', 'Amex', 'GiftCert', 'Special')")
    PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PayOwed', 'Owed', 'BalanceChange') not null")
    TransactionType transactionType;

    @ManyToOne
    @JsonIgnore
    Account account;
}