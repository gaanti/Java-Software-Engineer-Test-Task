package com.example.demo.services;

import com.example.demo.domains.Studio;
import com.example.demo.domains.lessons.*;
import com.example.demo.domains.users.Student.Student;
import com.example.demo.domains.users.Teacher;
import com.example.demo.repositories.*;
import com.example.demo.security.user.JwtUser;
import com.example.demo.security.user.JwtUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class LessonService {
    @Autowired
    LessonsRepository lessonsRepository;

    @Autowired
    PlainLessonRepository plainLessonRepository;

    @Autowired
    CanceledLessonRepository canceledLessonRepository;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    JwtUserService jwtUserService;

    @Autowired
    LessonCreditRepository lessonCreditRepository;

    public Lesson detectAndSetType(Lesson lesson) {
        // Its check is there only one student in this lesson
        // It should implement privateLesson, but it wasn't implemented yet
        var lessonAttendance = lesson.getAttendance();
        try {
            if (lessonAttendance.size() == 1) {
                Long studentId = -1L;
                for (Attendance value : lessonAttendance) {
                    studentId = value.getStudent().getId();
                }
                var lessonsSize = lessonsRepository.findAllByStudentId(studentId).size();
                if (!(lessonsSize >= 5)) {
                    lesson.setLessonType("CB" + (lessonsSize + 1));
                }
            }
        } catch (NullPointerException e) {
        } finally {
        }
        return lesson;
    }

    Attendance cancelLesson(Long lessonId, Long studentId, String reason) {
        Lesson lesson = lessonsRepository.findById(lessonId).orElseThrow();
        for (Attendance a : lesson.getAttendance()) {
            if (a.getStudent().getId().equals(studentId)) {
                var canceledLesson = CanceledLesson.builder()
                        .attendance(a)
                        .reason(reason)
                        .build();
                canceledLessonRepository.save(canceledLesson);
                a.setCanceledLesson(canceledLesson);
                return attendanceRepository.save(a);
            }
        }
        return null;
    }

    public Attendance cancelLesson(Long lessonId, Long studentId, String reason, Authentication authentication) {
        JwtUser currentUser = jwtUserService.getCurrentUser();
        boolean isSuperAdmin = currentUser.getAuthorities().stream().map(e -> Objects.equals(e.getAuthority(), "ROLE_SUPER_ADMIN")).collect(Collectors.toSet()).contains(true);
        Lesson lesson = lessonsRepository.findById(lessonId).orElseThrow();
        if (!isSuperAdmin && !Objects.equals(currentUser.getId(), lesson.getTeacher().getId())) {
            throw new RuntimeException("Teacher don't have a right to create a lesson for another teacher");
        }
        return cancelLesson(lessonId, studentId, reason);
    }

    public List<Lesson> findAll() {
        return lessonsRepository.findAll();
    }

    public PlainLesson editLesson(PlainLesson lesson) {
        var mappedAttendances = ((lesson.getAttendance().stream()
                .map(attendant -> Attendance.builder()
                        .lesson(lesson)
                        .student(studentRepository.findById(attendant.getStudent().getId()).orElseThrow())
                        .build())
                .collect(Collectors.toSet())));
        Set<Attendance> savedAttendances = new HashSet<>(attendanceRepository.saveAll(mappedAttendances));
        lesson.setAttendance(savedAttendances);
        PlainLesson savedLesson = plainLessonRepository.save(lesson);
        return savedLesson;
    }

    public List<Lesson> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        return lessonsRepository.findByDateBetween(startDate, endDate);
    }
}
