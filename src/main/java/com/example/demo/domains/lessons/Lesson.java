package com.example.demo.domains.lessons;

import com.example.demo.domains.Studio;
import com.example.demo.domains.users.Student.Student;
import com.example.demo.domains.users.Teacher;
import com.fasterxml.jackson.annotation.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    Long id;

    LocalTime time;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    public LocalDate date;

    @ManyToOne
    @JsonIncludeProperties(value = {"id", "studios", "firstName", "lastName", "firstName"})
    Teacher teacher;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lesson", orphanRemoval = true)
    @JsonIgnoreProperties(value = {"lesson"})
    Set<Attendance> attendance;

    String lessonType;

    @ManyToOne
    Studio studio;

    String comment;

    public void setStudents(Set<Student> studentSet) {
        var attendance = studentSet.stream().map(student -> Attendance.builder()
                .lesson(this)
                .student(student)
                .build()
        ).collect(Collectors.toSet());
        this.setAttendance(attendance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lesson lesson)) return false;

        if (getId() != null ? !getId().equals(lesson.getId()) : lesson.getId() != null) return false;
        if (getTime() != lesson.getTime()) return false;
        if (getDate() != null ? !getDate().equals(lesson.getDate()) : lesson.getDate() != null) return false;
        if (getTeacher() != null ? !getTeacher().equals(lesson.getTeacher()) : lesson.getTeacher() != null)
            return false;
        if (getAttendance() != null ? !getAttendance().equals(lesson.getAttendance()) : lesson.getAttendance() != null)
            return false;
        if (getLessonType() != null ? !getLessonType().equals(lesson.getLessonType()) : lesson.getLessonType() != null)
            return false;
        if (getStudio() != null ? !getStudio().equals(lesson.getStudio()) : lesson.getStudio() != null) return false;
        return getComment() != null ? getComment().equals(lesson.getComment()) : lesson.getComment() == null;
    }

    // It is broken because we have both lesson and attendace each other in hashcode
    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getTime() != null ? getTime().hashCode() : 0);
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        result = 31 * result + (getTeacher() != null ? getTeacher().hashCode() : 0);
        result = 31 * result + (getAttendance() != null ? getAttendance().hashCode() : 0);
        result = 31 * result + (getLessonType() != null ? getLessonType().hashCode() : 0);
        result = 31 * result + (getStudio() != null ? getStudio().hashCode() : 0);
        result = 31 * result + (getComment() != null ? getComment().hashCode() : 0);
        return result;
    }
}