package com.example.demo.domains.users;

import com.example.demo.BaseEntity;
import com.example.demo.domains.Company;
import com.example.demo.domains.Studio;
import com.example.demo.domains.lessons.LessonCredit;
import com.example.demo.security.user.JwtUser;
import com.example.demo.security.user.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@Table(name = "registration_voucher")
public class RegistrationVoucher  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp date;

    @ManyToOne
    JwtUser issuer;

    boolean isExecuted;

    @OneToOne
    JwtUser registerer;

    @ManyToMany
    @JoinColumn(unique = false)
    Set<Studio> studios;

    @Enumerated(EnumType.STRING)
    @Column(name = "accountType", columnDefinition = "enum ('Owner', 'Receptionist', 'Teacher', 'Student')", nullable = false, updatable = false)
    Position accountType;


    final String code = UUID.randomUUID().toString();
}
