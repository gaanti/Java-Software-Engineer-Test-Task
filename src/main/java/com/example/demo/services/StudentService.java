package com.example.demo.services;

import com.example.demo.domains.Studio;
import com.example.demo.domains.lessons.*;
import com.example.demo.domains.users.Teacher;
import com.example.demo.repositories.LessonsRepository;
import com.example.demo.domains.users.Student.Student;
import com.example.demo.repositories.StudentRepository;
import com.example.demo.security.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    LessonsRepository lessonsRepository;
    @Autowired
    JwtUserService userService;

    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    public Student getById(Long id) {
        return studentRepository.findById(id).orElseThrow();
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public Set<Lesson> getLessons(Student student) {
        return student.getAttendance().stream().map(Attendance::getLesson).collect(Collectors.toSet());
    }

    public List<Student> getStudentsForStatistics() {
        return studentRepository.findAll().stream().peek((st) -> {
//            var lessons = st.getLessons().stream().filter(lsn -> lsn.getType() == LessonType.PLAIN || lsn.getType() == LessonType.CONFIDENCE_BUILDER).collect(Collectors.toSet());
            var lessons = getLessons(st);
            var attendance = lessons.stream().map(l -> Attendance.builder()
                    .lesson(l)
                    .student(st)
                    .build()).collect(Collectors.toSet());
            st.setAttendance(attendance);
        }).collect(Collectors.toList());
    }
}
