package com.example.demo.repositories;

import com.example.demo.domains.Studio;
import com.example.demo.domains.lessons.LessonCredit;
import com.example.demo.domains.users.Student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LessonCreditRepository extends JpaRepository<LessonCredit, Long> {
}
