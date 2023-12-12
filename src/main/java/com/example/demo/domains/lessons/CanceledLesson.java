package com.example.demo.domains.lessons;

import com.example.demo.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CanceledLesson extends BaseEntity {
	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore
    Attendance attendance;
	@Column(name = "reason", columnDefinition = "TEXT")
	String reason;
	@CreationTimestamp
	@Column(updatable = false)
	Timestamp cancellationDate;
}
