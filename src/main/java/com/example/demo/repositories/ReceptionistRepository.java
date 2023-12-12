package com.example.demo.repositories;

import com.example.demo.domains.users.Receptionist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceptionistRepository extends JpaRepository<Receptionist, Long> {
}
