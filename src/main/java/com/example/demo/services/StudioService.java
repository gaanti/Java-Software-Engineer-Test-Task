package com.example.demo.services;

import com.example.demo.repositories.LessonsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class StudioService {

    @Autowired
    LessonService lessonService;

    @Autowired
    LessonsRepository lessonsRepository;

    Object getStatistics(@Value("${studio.lesson.price}") String lessonCost, LocalDate startDate, LocalDate endDate) {
        //Profit by month
        //The most active teacher
        //The most popular Activities
        //New Students
        //Studio load

        // I can not simply fetch take all lessons length and multiply it by price.
        // Some of them are group lessons, some are CB, some are cancelled and some are unattended

        var lessons = lessonService.findByDateBetween(startDate, endDate);

        return null;
    }
}
