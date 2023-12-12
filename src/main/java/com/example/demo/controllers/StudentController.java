package com.example.demo.controllers;

import com.example.demo.domains.Studio;
import com.example.demo.domains.users.Student.Student;
import com.example.demo.domains.users.Student.accounts.TransactionRepository;
import com.example.demo.repositories.StudentRepository;
import com.example.demo.security.user.JwtUserService;
import com.example.demo.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "false")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentService studentService;

    @Autowired
    JwtUserService userService;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/get-paged")
    List<Student> getStudentsPaged() {
        return studentRepository.findAll();
    }

    @GetMapping("/get/{studentId}")
    Student getStudent(@PathVariable Long studentId) {
        return studentRepository.findById(studentId).orElseThrow();
    }

    @DeleteMapping("/remove-transaction")
    void removeTransaction(@RequestParam(name = "transactionId") long transactionId) {
        transactionRepository.deleteById(transactionId);
    }
}

