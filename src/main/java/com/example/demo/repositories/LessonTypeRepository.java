package com.example.demo.repositories;

import com.example.demo.domains.lessons.LessonType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonTypeRepository extends JpaRepository<LessonType, Long> {
}
