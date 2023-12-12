package com.example.demo.controllers;

import com.example.demo.domains.BugReport;
import com.example.demo.domains.Studio;
import com.example.demo.domains.lessons.ScheduleSlot;
import com.example.demo.domains.users.Owner;
import com.example.demo.repositories.ScheduleSlotRepository;
import com.example.demo.repositories.StudioRepository;
import com.example.demo.security.user.JwtUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/studios")
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "false")
public class StudioController {

    @Autowired
    StudioRepository studioRepository;

    @Autowired
    HttpSession session;

    @Autowired
    JwtUserService userService;

    @Autowired
    ScheduleSlotRepository scheduleSlotRepository;


    @GetMapping("/get")
    List<Studio> getAllStudios() {
        return studioRepository.findAll();
    }

    @PostMapping("/add")
    Studio addStudio(@RequestBody Studio studio) {
        var currentUser = userService.getCurrentUser();
        if (userService.getCurrentUser() instanceof Owner) {
            var owner = (Owner) currentUser;
            studio.setCompany(owner.getCompany());
            return studioRepository.save(studio);
        } else throw new RuntimeException("You have no right to add new studio");
    }

    @PutMapping("/update-time-slots/{studioId}")
    ScheduleSlot selectStudios(@RequestParam String newTimeString,
                               @RequestParam String oldTimeString,
                               @PathVariable Long studioId) {
        var newTimeHoursAndMinutes = newTimeString.split(":");
        var oldTimeHoursAndMinutes = oldTimeString.split(":");
        var newTime = LocalTime.of(Integer.parseInt(newTimeHoursAndMinutes[0]), Integer.parseInt(newTimeHoursAndMinutes[1]));
        var oldTime = LocalTime.of(Integer.parseInt(oldTimeHoursAndMinutes[0]), Integer.parseInt(oldTimeHoursAndMinutes[1]));
        var scheduleSlot = scheduleSlotRepository.findByTimeAndStudioId(oldTime, studioId).orElseThrow();
        scheduleSlot.setTime(newTime);
        return scheduleSlotRepository.save(scheduleSlot);
    }

    @PutMapping("/add-time-slots/{studioId}")
    ScheduleSlot selectStudios(@RequestParam String timeString,
                               @PathVariable Long studioId) {
        var timeHoursAndMinutes = timeString.split(":");
        var time = LocalTime.of(Integer.parseInt(timeHoursAndMinutes[0]), Integer.parseInt(timeHoursAndMinutes[1]));
        ScheduleSlot scheduleSlot = new ScheduleSlot(time, new Studio(studioId));
        if (scheduleSlotRepository.findByTimeAndStudioId(time, studioId).isPresent()) {
            throw new RuntimeException("Time slot is already in schedule");
        } else {
            return scheduleSlotRepository.save(scheduleSlot);
        }
    }

}