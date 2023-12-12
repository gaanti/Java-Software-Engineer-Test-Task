package com.example.demo;

import com.example.demo.domains.users.Receptionist;
import com.example.demo.domains.users.Teacher;
import com.example.demo.repositories.*;
import com.example.demo.security.user.JwtUserRepository;
import com.example.demo.security.user.JwtUserService;
import com.example.demo.services.LessonService;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
@Order(2)
public class initStaff implements CommandLineRunner {

    @Value("${app.init-fake-data}")
    private Boolean init;
    private static final int NUM_THREADS = 8;

    @Autowired
    LessonService lessonService;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    LessonsRepository lessonsRepository;
    @Autowired
    PlainLessonRepository plainLessonRepository;
    @Autowired
    JwtUserRepository userRepository;
    @Autowired
    AttendanceRepository attendanceRepository;
    @Autowired
    LessonTypeRepository lessonTypeRepository;
    @Autowired
    StudioRepository studioRepository;
    @Autowired
    JwtUserService jwtUserService;

    @Autowired
    ReceptionistRepository receptionistRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) {
        if (init && receptionistRepository.findAll().isEmpty()) {
            init();
        }
    }

    public void init() {
        var random = new Random();
        var allUsers = userRepository.findAll();

        // // users
        Faker faker = new Faker();
        var studios = studioRepository.findAll();
        // teacher
        Runnable initTeachers = () -> {
            var firstName = faker.name().firstName();
            var lastName = faker.name().lastName();
            Teacher user = Teacher.builder()
                    .username(faker.name().username())
                    .firstName(firstName)
                    .lastName(lastName)
                    .email("%s.%s@gmail.kom".formatted(firstName, lastName))
                    .password("test123")
                    .birthday(LocalDate.now().minusDays(random.nextInt(30)).minusMonths(random.nextInt(12)).minusYears(random.nextInt(40) + 10))
                    .build();
            var studioSelection = random.nextInt(0, studios.size());
            if (studioSelection == studios.size()) {
                user.setStudios(new HashSet<>(studios));
            } else {
                user.setStudios(Set.of(studios.get(studioSelection)));
            }
            jwtUserService.create(user);
        };
        executeInMultiThread(5, initTeachers);

        Runnable initReceptionists = () -> {
            var firstName = faker.name().firstName();
            var lastName = faker.name().lastName();
            var user = Receptionist.builder()
                    .username(faker.name().username())
                    .firstName(firstName)
                    .lastName(lastName)
                    .email("%s.%s@gmail.kom".formatted(firstName, lastName))
                    .password("test123")
                    .birthday(LocalDate.now().minusDays(random.nextInt(30)).minusMonths(random.nextInt(12)).minusYears(random.nextInt(40) + 10))
                    .build();

            var studioSelection = random.nextInt(0, studios.size());
            if (studioSelection == studios.size()) {
                user.setStudios(new HashSet<>(studios));
            } else {
                user.setStudios(Set.of(studios.get(studioSelection)));
            }
            jwtUserService.create(user);
        };
        executeInMultiThread(3, initReceptionists);
    }


    public void executeInMultiThread(int iterations, Runnable runnable) {
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        CountDownLatch latch = new CountDownLatch(iterations); // Number of iterations
        for (int i = 0; i < iterations; i++) {
//            final int iteration = i; // Variable needs to be effectively final to use inside the lambda expression
            executorService.submit(() -> {
                try {
                    runnable.run();
//                    System.out.println("Iteration " + iteration + " completed.");
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await(); // Wait for all iterations to complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
