package com.example.demo.domains;

import com.example.demo.domains.lessons.ScheduleSlot;
import com.example.demo.domains.lessons.LessonType;
import com.example.demo.domains.users.Owner;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "studios")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Studio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JoinColumn(nullable = false)
    String studioName;

    @JoinColumn(nullable = false)
    String abbreviation;

    @JoinColumn(nullable = false)
    String address;

    @ManyToOne
//    @JoinColumn(name = "company_id")
    @JsonIgnore
    Company company;

    // Schedule
    @OneToMany(cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Set<LessonType> lessonTypes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studio", fetch = FetchType.EAGER)
    Set<ScheduleSlot> scheduleSlots;

    public Studio(Long studioId) {
        this.setId(studioId);
    }
}
