package com.example.demo;

import com.example.demo.domains.Studio;
import com.example.demo.domains.lessons.*;
import com.example.demo.domains.users.Student.Student;
import com.example.demo.domains.users.Student.accounts.Account;
import com.example.demo.domains.users.Student.accounts.PaymentType;
import com.example.demo.domains.users.Student.accounts.Transaction;
import com.example.demo.domains.users.Student.accounts.TransactionType;
import com.example.demo.domains.users.Teacher;
import com.example.demo.repositories.*;
import com.example.demo.security.user.*;
import com.example.demo.services.LessonService;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Order(3)
public class initLessonsAndStudents implements CommandLineRunner {

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
    private LessonCreditRepository lessonCreditRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private ReceptionistRepository receptionistRepository;
    @Autowired
    private TeacherRepository teacherRepository;


    @Override
    public void run(String... args) {
        if (init && plainLessonRepository.findAll().isEmpty()) {
//        if (false) {
            init();
        }
    }

    public void init() {
        var random = new Random();
        var receptionists = receptionistRepository.findAll();

        // // users
        Faker faker = new Faker();
        var studios = studioRepository.findAll();

        // students
        var teachers = teacherRepository.findAll();
        Runnable initStudents = () -> {
            var firstName = faker.name().firstName();
            var lastName = faker.name().lastName();
            var account = Account.builder()
                    .build();
            account.setTransactions(Arrays.stream(new int[random.nextInt(0, 20)]).mapToObj(t -> Transaction.builder()
                    .value(random.nextFloat(48.0f, 480.0f))
                    .account(account)
                    .paymentType(PaymentType.Cash)
                    .transactionType(TransactionType.BalanceChange)
                    .accepter(receptionists.get(random.nextInt(0, receptionists.size())))
                    .build()).collect(Collectors.toSet()));
            var localDate = LocalDate.now().minusMonths(random.nextInt(0, 12)).minusDays(random.nextInt(0, 30));
            var user = Student.builder()
                    .studio(studios.get(random.nextInt(0, studios.size())))
                    .account(account)
                    .teachers(getRandomTeachersSet(teachers, random))
                    .username(faker.name().username())
                    .firstName(firstName)
                    .lastName(lastName)
                    .registered(Timestamp.valueOf(LocalDateTime.of(localDate, LocalTime.now())))
                    .email("%s.%s@gmail.kom".formatted(firstName, lastName))
                    .phoneNumber(faker.phoneNumber().phoneNumber())
                    .password(passwordEncoder.encode("test123"))
                    .birthday(LocalDate.now().minusDays(random.nextInt(30)).minusMonths(random.nextInt(12)).minusYears(random.nextInt(40) + 10))
                    .homeAddress(faker.address().fullAddress())
                    .build();
            jwtUserService.create(user);
        };
        executeInMultiThread(80, initStudents);

        // lessons
        var studio = studios.get(random.nextInt(0, 2));
        var lessonTimes = studio.getScheduleSlots();
        List<ScheduleSlot> lessonTimesList = new ArrayList<>(lessonTimes);
        var students = studentRepository.findAll();
        Runnable initLessons = () -> {
            var randomTime = lessonTimesList.get(random.nextInt(0, lessonTimes.size()));
            var randomTeacher = teachers.get(random.nextInt(0, teachers.size()));
            var randomStudent = students.get(random.nextInt(0, students.size()));
//            var randomStudent = students.get(1);
            var localDate = LocalDate.now().minusMonths(random.nextInt(0, 12)).plusMonths(random.nextInt(0, 12)).minusDays(random.nextInt(0, 30)).plusDays(random.nextInt(0, 30));

            planLesson(randomStudent, randomTeacher, localDate, randomTime.getTime(), studio);
            giveACreditToStudent(randomTeacher, randomStudent, LocalDateTime.of(localDate, LocalTime.now()));

        };
        executeInMultiThread(1380, initLessons);
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

    public void giveACreditToStudent(JwtUser accepter, Student student, LocalDateTime purchaseTime) {
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


    public void planLesson(Student student, Teacher teacher, LocalDate date, LocalTime time, Studio studio) {
        // build lesson
        PlainLesson lesson = PlainLesson.builder()
                .studio(studio)
                .teacher(teacher)
                .time(time)
                .date(date)
                .build();
        try {
            lesson.setAttendance(Set.of(Attendance.builder()
                    .student(student)
                    .lesson(lesson)
                    .build()));
        } catch (NullPointerException e) {
            lesson.setAttendance(new HashSet<>());
        }
        var checkedLesson = lessonService.detectAndSetType(lesson);
        lessonsRepository.save(checkedLesson);
    }

    public Set<Teacher> getRandomTeachersSet(List<Teacher> teachers, Random random) {
        Set<Teacher> selectedTeachers = new HashSet<>();
        for (Teacher teacher : teachers) {
            if (random.nextBoolean()) {
                selectedTeachers.add(teacher);
            }
        }
        return selectedTeachers;
    }
}
