package com.example.demo.domains.lessons;

import javax.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class PlainLesson extends Lesson{
}
