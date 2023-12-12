package com.example.demo.repositories;

import com.example.demo.domains.lessons.PlainLesson;
import com.example.demo.domains.users.Teacher;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PlainLessonRepository extends JpaRepository<PlainLesson, Long> {
    void deleteById (@NotNull Long id);
}
