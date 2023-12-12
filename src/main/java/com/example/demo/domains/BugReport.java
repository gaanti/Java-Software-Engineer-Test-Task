package com.example.demo.domains;

import com.example.demo.BaseEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugReport extends BaseEntity {
    @CreationTimestamp
    @Column(updatable = false)
    Timestamp creationTime;
    String title;
    String description;
}
