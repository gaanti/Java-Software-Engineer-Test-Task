package com.example.demo.domains.users;

import com.example.demo.security.user.JwtUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RegistrationVoucherRepository extends JpaRepository<RegistrationVoucher, Long> {

    @Query("select r from RegistrationVoucher r where r.issuer = ?1")
    Page<RegistrationVoucher> findAllByIssuer(JwtUser issuer, Pageable pageable);



    Optional<RegistrationVoucher> findByCode(String code);
}