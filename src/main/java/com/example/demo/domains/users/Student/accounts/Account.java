package com.example.demo.domains.users.Student.accounts;

import com.example.demo.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseEntity {
    @Transient
    public float getBalance() {
        if (transactions != null) {
            return transactions.stream()
                    .filter(t -> t.transactionType.equals(TransactionType.BalanceChange))
                    .map(Transaction::getValue)
                    .reduce((float) 0, Float::sum);
        }
        return (float) 0;
    }

    @Transient
    public float getOwed() {
        if (transactions != null) {
            return transactions.stream()
                    .filter(t -> t.transactionType.equals(TransactionType.Owed) || t.transactionType.equals(TransactionType.PayOwed))
                    .map(t-> {
                        if (t.transactionType.equals(TransactionType.PayOwed)) {
                            t.setValue(t.getValue()*(-1));
                        }
                        return t.getValue();
                    }).reduce((float) 0, Float::sum);
        }
        return (float) 0;
    }



    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"accepter"})
    Set<Transaction> transactions;
}
