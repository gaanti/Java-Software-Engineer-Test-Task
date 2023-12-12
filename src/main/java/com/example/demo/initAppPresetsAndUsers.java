package com.example.demo;

import com.example.demo.domains.Company;
import com.example.demo.domains.CompanyRepository;
import com.example.demo.domains.Studio;
import com.example.demo.domains.lessons.ScheduleSlot;
import com.example.demo.domains.lessons.LessonType;
import com.example.demo.domains.users.Owner;
import com.example.demo.repositories.StudioRepository;
import com.example.demo.security.user.JwtUserService;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Order(1)
public class initAppPresetsAndUsers implements CommandLineRunner {
    @Value("${app.init-fake-data}")
    private Boolean init;
    @Autowired
    StudioRepository studioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    JwtUserService userService;
    @Autowired
    private Flyway flyway;


//    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
//        flyway.baseline();
//        String sql = "DROP TABLE IF EXISTS flyway_schema_history";
//         Execute the SQL command
//        jdbcTemplate.execute(sql);

        if (studioRepository.findAll().isEmpty()) {
            Set<Studio> studios = new HashSet<>();
            var scheduleSlots = List.of(
                    LocalTime.of(8, 30),
                    LocalTime.of(9, 15),
                    LocalTime.of(10, 0),
                    LocalTime.of(10, 45),
                    LocalTime.of(11, 30),
                    LocalTime.of(12, 15),
                    LocalTime.of(13, 0),
                    LocalTime.of(13, 45),
                    LocalTime.of(14, 30),
                    LocalTime.of(15, 15),
                    LocalTime.of(16, 0),
                    LocalTime.of(16, 45),
                    LocalTime.of(17, 30),
                    LocalTime.of(18, 15),
                    LocalTime.of(19, 0),
                    LocalTime.of(19, 45),
                    LocalTime.of(20, 30),
                    LocalTime.of(21, 15)
            );

            if (init) {
                var VNC = Studio.builder()
                        .studioName("Venice")
                        .abbreviation("VNC")
                        .lessonTypes(createLessonTypes())
                        .address("123 Amezinos, Venice, FL")
                        .build();
                VNC.setScheduleSlots(getScheduleSlotsForStudio(scheduleSlots, VNC));
                studios.add(VNC);
            }
            var SRQ = Studio.builder()
                    .studioName("Sarasota")
                    .abbreviation("SRQ")
                    .lessonTypes(createLessonTypes())
                    .address("2272 Main St, Sarasota, FL 34237")
                    .build();
            SRQ.setScheduleSlots(getScheduleSlotsForStudio(scheduleSlots, SRQ));
            studios.add(SRQ);
            Company company = Company.builder()
                    .name("Dynasty Dance Clubs")
                    .preferredColor("Rose")
                    .studios(studios)
                    .build();
            var owner = Owner.builder()
                    .username("mlot")
                    .company(company)
                    .firstName("Maxym")
                    .lastName("Lototsky")
                    .email("owner@gmail.com")
                    .birthday(LocalDate.now().minusYears(100))
                    .password(passwordEncoder.encode("test123#"))
//                    .password("test123#")
                    .enabled(true)
                    .build();
            if (!init) {
                owner.setPassword(passwordEncoder.encode("#48BeaniesInMyHeart"));
            }
            var savedCompany = companyRepository.save(company);
            owner.setCompany(savedCompany);
            userService.save(owner);
            company.setOwner(owner);
            companyRepository.save(savedCompany);
            studioRepository.saveAll(studios);
        }
    }

    public Set<LessonType> createLessonTypes() {
        var set = new HashSet<LessonType>();
        set.add(new LessonType("CB1"));
        set.add(new LessonType("CB2"));
        set.add(new LessonType("CB3"));
        set.add(new LessonType("CB4"));
        set.add(new LessonType("CB5"));
        return set;
    }


    public Set<ScheduleSlot> getScheduleSlotsForStudio(List<LocalTime> times, Studio studio) {
        return times.stream().map(t -> new ScheduleSlot(t, studio)).collect(Collectors.toSet());
    }

}
