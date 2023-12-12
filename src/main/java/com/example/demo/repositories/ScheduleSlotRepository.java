package com.example.demo.repositories;

import com.example.demo.domains.lessons.ScheduleSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.Optional;

public interface ScheduleSlotRepository extends JpaRepository<ScheduleSlot, Long> {
    Optional<ScheduleSlot> findByTimeAndStudioId(LocalTime time, Long studio_id);

}
