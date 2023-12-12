package com.example.demo.domains.lessons;

import com.example.demo.BaseEntity;
import com.example.demo.domains.Studio;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleSlot extends BaseEntity {
    private LocalTime time;

    @ManyToOne
    @JsonIgnore
    Studio studio;
}