package com.example.demo.domains.lessons;

import com.example.demo.BaseEntity;
import com.example.demo.domains.users.Student.Student;
import com.example.demo.security.user.JwtUser;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.LocalDateTime;

@Transactional
@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonCredit extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM ('USED', 'AVAILABLE', 'CANCELLED') not null", nullable = false)
    Status status;
    LocalDateTime purchaseTime;

    @ManyToOne
    @JsonIncludeProperties(value = {"id", "firstName", "lastName"})
    JwtUser accepter;

    int amount;

//    @Value("${studio.lesson.price}")
    private double price = 110.00;

    //    @JsonIgnore
    @ManyToOne
    @JsonIncludeProperties(value = {"id", "firstName", "lastName"})
    Student student;

    public enum Status {
        AVAILABLE,
        CANCELLED,
    }
}