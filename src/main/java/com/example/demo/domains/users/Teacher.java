package com.example.demo.domains.users;

import com.example.demo.domains.Studio;
import com.example.demo.domains.lessons.Lesson;
import com.example.demo.domains.users.Student.Student;
import com.example.demo.security.user.JwtUser;
import com.example.demo.security.user.Position;
import com.example.demo.security.user.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = "teachers")
@Getter
@Setter
public class Teacher extends JwtUser {

    {
        this.setRole(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER));
        this.setPosition(Position.Teacher);
    }

    @OneToMany(mappedBy = "teacher", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    Set<Lesson> lessons = new HashSet<>();

    @Formula(value = "(SELECT COUNT(*) FROM plain_lesson pl WHERE pl.teacher_id = id)")
    private Integer lessonCount;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIncludeProperties(value = {"studioName", "abbreviation", "id", "scheduleSlots"})
    Set<Studio> studios;

    @ManyToMany(mappedBy = "teachers")
    @JsonIncludeProperties(value = {"id", "firstName", "LastName"})
    @JsonIgnore
    Set<Student> students;

    public Teacher(JwtUser jwtUser) {
        super(jwtUser);
    }

    public Teacher(Long teacherId) {
        super(teacherId);
    }

}