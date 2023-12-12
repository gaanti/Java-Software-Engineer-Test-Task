package com.example.demo.repositories;

import com.example.demo.domains.lessons.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
