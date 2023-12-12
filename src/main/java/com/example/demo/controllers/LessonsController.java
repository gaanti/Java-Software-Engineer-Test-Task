package com.example.demo.controllers;

import com.example.demo.domains.lessons.*;
import com.example.demo.repositories.*;
import com.example.demo.security.user.JwtUserService;
import com.example.demo.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lessons")
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "false")
public class LessonsController {
    @Autowired
    LessonService lessonService;

    @Autowired
    LessonsRepository lessonsRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    JwtUserService userService;

    @Autowired
    LessonCreditRepository lessonCreditRepository;

    @Autowired
    PlainLessonRepository plainLessonRepository;

    @GetMapping("/get/{lessonId}")
    Lesson getLesson(@PathVariable Long lessonId) {
        return lessonsRepository.findById(lessonId).orElseThrow();
    }

    @DeleteMapping("/cancel/{lessonId}")
    Attendance cancelLesson(@PathVariable Long lessonId, @RequestParam(name = "studentId") Long studentId, @RequestBody(required = false) String reason, Authentication authentication) {
        return lessonService.cancelLesson(lessonId, studentId, reason, authentication);
    }

    @PutMapping("/update")
    Lesson updateLesson(@RequestBody PlainLesson lesson) {
        return lessonService.editLesson(lesson);
    }

}