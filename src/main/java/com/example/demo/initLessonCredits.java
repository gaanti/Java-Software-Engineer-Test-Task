package com.example.demo;

import com.example.demo.domains.lessons.LessonCredit;
import com.example.demo.repositories.LessonCreditRepository;
import com.example.demo.repositories.StudentRepository;
import com.example.demo.repositories.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Order(5)
public class initLessonCredits implements CommandLineRunner {

	@Value("${app.init-fake-data}")
	private Boolean init;


	@Autowired
	private LessonCreditRepository lessonCreditRepository;

	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private TeacherRepository teacherRepository;

	@Override
	public void run(String... args) {
		if (init && lessonCreditRepository.findAll().isEmpty()) {
			init();
		}
	}

	public void init() {
		var accepter = teacherRepository.findAll().get(0);
		var student = studentRepository.findAll().get(0);
		var purchaseTime = LocalDateTime.now();
		var status = LessonCredit.Status.AVAILABLE;
		var lessonCredit = LessonCredit.builder()
				.accepter(accepter)
				.student(student)
				.purchaseTime(purchaseTime)
				.status(status)
				.amount(1)
				.price(110.00)
				.build();
		lessonCreditRepository.save(lessonCredit);
	}
}
