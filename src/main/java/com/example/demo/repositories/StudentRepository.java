package com.example.demo.repositories;

import com.example.demo.domains.Studio;
import com.example.demo.domains.users.Student.Student;
import com.example.demo.domains.users.Teacher;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface StudentRepository extends JpaRepository<Student, Long> {
	Page<Student> findAll(Specification<Student> specification, Pageable pageable);
}