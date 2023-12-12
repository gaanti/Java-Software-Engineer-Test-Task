package com.example.demo.controllers;

import com.example.demo.domains.Studio;
import com.example.demo.domains.lessons.Lesson;
import com.example.demo.domains.users.Teacher;
import com.example.demo.repositories.TeacherRepository;
import com.example.demo.security.user.JwtUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/teachers")
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "false")
public class TeacherController {
    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    JwtUserService userService;

    @GetMapping("/get")
    List<Teacher> getTeachers() {
        return teacherRepository.findAll();
    }

    @GetMapping("/get/{teacherId}")
    Teacher getTeacher(@PathVariable Long teacherId) {
        return teacherRepository.findById(teacherId).orElseThrow();
    }

    @GetMapping("/get/myself")
    Teacher getMyself(Authentication authentication) {
        return teacherRepository.findByUsername(authentication.getPrincipal().toString()).orElseThrow();
    }

    @GetMapping("/get/schedule/{teacherId}")
    Set<Lesson> getTeacherSchedule(@PathVariable Long teacherId) {
        return teacherRepository.findById(teacherId).orElseThrow().getLessons();
    }

    @PostMapping("/create")
    Teacher createTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @PutMapping("/update-studio/{teacherId}")
    Teacher createTeacher(@RequestBody Set<Studio> studios, @PathVariable Long teacherId) {
        var teacher = teacherRepository.findById(teacherId).orElseThrow();
        teacher.setStudios(studios);
        return teacherRepository.save(teacher);
    }
}
