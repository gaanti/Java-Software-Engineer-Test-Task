package com.example.demo.domains.lessons;

import com.example.demo.domains.users.Student.Student;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JsonIncludeProperties(value = {"id", "time", "date"})
    Lesson lesson;

    @ManyToOne
    @JsonIncludeProperties(value = {"attendance", "lessonCredits", "firstName", "lastName", "id", "studio"})
    Student student;

    @JsonProperty("isAttended")
    public boolean isAttended;

    // fetch = FetchType.LAZY is the solution for the undo cancellation
    // For some reason there must be CascadeType.REMOVE, CascadeType.ALL at the same time.
    // Only CascadeType.ALL doesn't work
    @OneToOne(mappedBy = "attendance", cascade = {CascadeType.REMOVE, CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.LAZY)
    CanceledLesson canceledLesson;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attendance that)) return false;

        if (isAttended() != that.isAttended()) return false;
        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getLesson() != null ? !getLesson().equals(that.getLesson()) : that.getLesson() != null) return false;
        return getStudent() != null ? getStudent().equals(that.getStudent()) : that.getStudent() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getStudent() != null ? getStudent().hashCode() : 0);
        result = 31 * result + (isAttended() ? 1 : 0);
        return result;
    }


    // HashCode was read only from it, so I need to fix it in order to be fix the error with single value in array

}
